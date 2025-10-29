package com.bpa4j.util.codegen.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bpa4j.util.codegen.EditableNode;
import com.bpa4j.util.codegen.EditableNode.Property;
import com.bpa4j.util.codegen.EditableNode.Property.PropertyType;
import com.bpa4j.util.codegen.FeatureConfigNode;
import com.bpa4j.util.codegen.FeatureNode;
import com.bpa4j.util.codegen.ProjectGraph;
import com.bpa4j.util.codegen.ProjectNode;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.HelpEntry;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.Instruction;
import com.bpa4j.util.codegen.ProjectGraph.Problem;
import com.bpa4j.util.codegen.PermissionsNode;
import com.bpa4j.util.codegen.RolesNode;
import com.bpa4j.util.codegen.RolesNode.RoleRepresentation;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Root;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Read-only REST endpoints for ProjectGraph structure.
 * Base path: /graph
 * @author AI-generated
 */
@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphResource{
	@Inject
	private ProjectGraph graph;

	@GET
	public Map<String,Object>summary(){
		Map<String,Long>counts=graph.nodes.stream().collect(Collectors.groupingBy(n->n.getClass().getSimpleName(),Collectors.counting()));
		Map<String,Object>m=new HashMap<>();
		m.put("projectFolder",new File(graph.projectFolder.getPath()).getPath());
		m.put("counts",counts);
		return m;
	}

	@GET
	@Path("/problems")
	public List<Map<String,Object>>problems(){
		List<Problem>ps=graph.findProblems();
		List<Map<String,Object>>out=new ArrayList<>();
		for(Problem p:ps){
			Map<String,Object>m=new HashMap<>();
			m.put("message",p.message);
			m.put("type",p.type.name());
			out.add(m);
		}
		return out;
	}

	@GET
	@Path("/editables")
	public List<Map<String,Object>>editables(){
		return graph.nodes.stream().filter(n->n instanceof EditableNode).map(n->{
			EditableNode e=(EditableNode)n;
			Map<String,Object>m=new HashMap<>();
			m.put("name",e.name);
			m.put("objectName",e.objectName);
			List<Map<String,Object>>props=new ArrayList<>();
			for(EditableNode.Property p:e.properties){
				Map<String,Object>pm=new HashMap<>();
				pm.put("name",p.name);
				pm.put("type",p.type==null?null:p.type.name());
				props.add(pm);
			}
			m.put("properties",props);
			return m;
		}).toList();
	}

	@POST
	@Path("/editables")
	public Map<String,Object>createEditable(Map<String,Object>body){
		String name=(String)body.get("name");
		String objectName=(String)body.get("objectName");
		List<?>rawProps=(List<?>)body.getOrDefault("properties",List.of());
		List<Map<String,String>>props=new ArrayList<>();
		for(Object o:rawProps)if(o instanceof Map<?,?> m){
			Map<String,String>pm=new HashMap<>();
			for(Map.Entry<?,?>e:m.entrySet())if(e.getKey()!=null&&e.getValue()!=null)pm.put(e.getKey().toString(),e.getValue().toString());
			props.add(pm);
		}
		List<Property>properties=new ArrayList<>();
		for(Map<String,String>p:props){
			String pn=p.get("name");
			String pt=p.get("type");
			properties.add(new Property(pn,pt==null?null:PropertyType.valueOf(pt)));
		}
		EditableNode en=graph.createEditableNode(name,objectName,properties.toArray(new Property[0]));
		return Map.of("status","ok","name",en.name);
	}

	@DELETE
	@Path("/editables/{name}")
	public Map<String,Object>deleteEditable(@PathParam("name")String name){
		EditableNode en=(EditableNode)graph.nodes.stream().filter(n->n instanceof EditableNode&&((EditableNode)n).name.equals(name)).findAny().orElse(null);
		if(en==null)throw new IllegalArgumentException("Editable not found: "+name);
		graph.deleteNode(en);
		return Map.of("status","ok");
	}

	@GET
	@Path("/permissions")
	public List<String>permissions(){
		PermissionsNode pn=(PermissionsNode)graph.nodes.stream().filter(n->n instanceof PermissionsNode).findAny().orElse(null);
		return pn==null?List.of():pn.permissions;
	}

	@POST
	@Path("/permissions")
	public Map<String,Object>addPermission(Map<String,String>body){
		String permission=body.get("permission");
		PermissionsNode pn=(PermissionsNode)graph.nodes.stream().filter(n->n instanceof PermissionsNode).findAny().orElse(null);
		if(pn==null)throw new IllegalStateException("PermissionsNode not found");
		pn.addPermission(permission);
		return Map.of("status","ok");
	}

	@DELETE
	@Path("/permissions/{perm}")
	public Map<String,Object>removePermission(@PathParam("perm")String perm){
		PermissionsNode pn=(PermissionsNode)graph.nodes.stream().filter(n->n instanceof PermissionsNode).findAny().orElse(null);
		if(pn==null)throw new IllegalStateException("PermissionsNode not found");
		pn.removePermission(perm);
		return Map.of("status","ok");
	}

	@GET
	@Path("/roles")
	public List<Map<String,Object>>roles(){
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)return List.of();
		List<Map<String,Object>>out=new ArrayList<>();
		for(RoleRepresentation r:rn.roles){
			Map<String,Object>m=new HashMap<>();
			m.put("name",r.name);
			m.put("permissions",r.permissions==null?List.of():new ArrayList<>(r.permissions));
			out.add(m);
		}
		return out;
	}

	@POST
	@Path("/roles")
	public Map<String,Object>addRole(Map<String,String>body){
		String name=body.get("name");
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)throw new IllegalStateException("RolesNode not found");
		rn.addRole(name);
		return Map.of("status","ok");
	}

	@DELETE
	@Path("/roles/{role}")
	public Map<String,Object>removeRole(@PathParam("role")String role){
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)throw new IllegalStateException("RolesNode not found");
		rn.removeRole(role);
		return Map.of("status","ok");
	}

	@POST
	@Path("/roles/{role}/permissions")
	public Map<String,Object>grantPermission(@PathParam("role")String role,Map<String,String>body){
		String permission=body.get("permission");
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)throw new IllegalStateException("RolesNode not found");
		rn.addPermission(role,permission);
		return Map.of("status","ok");
	}

	@DELETE
	@Path("/roles/{role}/permissions/{perm}")
	public Map<String,Object>revokePermission(@PathParam("role")String role,@PathParam("perm")String perm){
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)throw new IllegalStateException("RolesNode not found");
		rn.removePermission(role,perm);
		return Map.of("status","ok");
	}

	@GET
	@Path("/navigator")
	public List<Map<String,Object>>navigator(){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)return List.of();
		List<Map<String,Object>>out=new ArrayList<>();
		for(HelpEntry e:nn.entries){
			Map<String,Object>m=new HashMap<>();
			m.put("text",e.text);
			List<Map<String,Object>>ins=new ArrayList<>();
			for(Instruction i:e.instructions){
				Map<String,Object>im=new HashMap<>();
				im.put("type",i.type.name());
				im.put("text",i.text);
				ins.add(im);
			}
			m.put("instructions",ins);
			out.add(m);
		}
		return out;
	}

	@GET
	@Path("/features")
	public List<Map<String,Object>>features(){
		return graph.nodes.stream().filter(n->n instanceof FeatureConfigNode).map(GraphResource::nodeSummary).toList();
	}

	@POST
	@Path("/save-initial-state")
	public Map<String,Object>saveInitialState(){
		try{
			java.nio.file.Files.createDirectories(java.nio.file.Path.of(graph.projectFolder+"/resources/initial/"));
			java.nio.file.Files.copy(java.nio.file.Path.of(Root.folder+"Data.ser"+ProgramStarter.version),java.nio.file.Path.of(graph.projectFolder+"/resources/initial/Data.ser"),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			java.nio.file.Files.copy(java.nio.file.Path.of(Root.folder+"Users.ser"+ProgramStarter.version),java.nio.file.Path.of(graph.projectFolder+"/resources/initial/Users.ser"),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			return Map.of("status","ok");
		}catch(java.io.IOException ex){
			throw new IllegalStateException(ex);
		}
	}

	@GET
	@Path("/impl-features")
	public List<Map<String,Object>>implFeatures(){
		return graph.nodes.stream().filter(n->n instanceof FeatureNode).map(GraphResource::nodeSummary).toList();
	}

	private static Map<String,Object>nodeSummary(ProjectNode n){
		Map<String,Object>m=new HashMap<>();
		m.put("type",n.getClass().getSimpleName());
		m.put("path",n.location==null?null:n.location.getPath());
		return m;
	}
}


