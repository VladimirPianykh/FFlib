package com.bpa4j.util.codegen.server;

import java.io.File;
import java.io.IOException;
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
import com.bpa4j.util.codegen.PermissionsNode;
import com.bpa4j.util.codegen.ProjectGraph;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.HelpEntry;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.Instruction;
import com.bpa4j.util.codegen.ProjectGraph.Problem;
import com.bpa4j.util.codegen.ProjectNode;
import com.bpa4j.util.codegen.RolesNode;
import com.bpa4j.util.codegen.RolesNode.RoleRepresentation;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
	public List<Map<String,Object>>editables(@QueryParam("detailed") boolean detailed){
		return graph.nodes.stream().filter(n->n instanceof EditableNode).map(n->{
			EditableNode e=(EditableNode)n;
			Map<String,Object>m=new HashMap<>();
			m.put("name",e.name);
			m.put("objectName",e.objectName==null?"<no-name>":e.objectName);
			m.put("location",e.location==null?null:e.location.getPath());
			if(detailed){
				List<Map<String,Object>>props=new ArrayList<>();
				for(EditableNode.Property p:e.properties){
					Map<String,Object>pm=new HashMap<>();
					pm.put("name",p.getName());
					pm.put("type",p.getType()==null?null:p.getType().name());
					props.add(pm);
				}
				m.put("properties",props);
			}
			return m;
		}).toList();
	}

	@POST
	@Path("/editables")
	public Map<String,Object>createEditable(Map<String,Object>body)throws IOException{
		String name=(String)body.get("name");
		String objectName=(String)body.get("objectName");
		if(name.contains(" ")||name.isBlank())return Map.of("status","error","error","Name mustn't be blank.");
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
		return Map.of("status","ok","location",en.location.getPath());
	}

	@DELETE
	@Path("/editables/{name}")
	public Map<String,Object>deleteEditable(@PathParam("name")String name){
		EditableNode en=(EditableNode)graph.nodes.stream().filter(n->n instanceof EditableNode&&((EditableNode)n).name.equals(name)).findAny().orElse(null);
		if(en==null)throw new IllegalArgumentException("Editable not found: "+name);
		graph.deleteNode(en);
		return Map.of("status","ok");
	}

	@PUT
	@Path("/editables/{name}")
	public Map<String,Object>updateEditable(@PathParam("name")String name,Map<String,Object>body){
		EditableNode en=(EditableNode)graph.nodes.stream().filter(n->n instanceof EditableNode&&((EditableNode)n).name.equals(name)).findAny().orElse(null);
		if(en==null)throw new IllegalArgumentException("Editable not found: "+name);
		String newName=(String)body.get("newName");
		String newObjectName=(String)body.get("newObjectName");
		if(newName!=null&&!newName.isBlank())en.changeNameIn(graph,newName);
		if(newObjectName!=null&&!newObjectName.isBlank())en.changeObjectName(newObjectName);
		List<?>addPropsRaw=(List<?>)body.getOrDefault("addProperties",List.of());
		for(Object o:addPropsRaw)if(o instanceof Map<?,?> m){
			String pn=String.valueOf(m.get("name"));
			String pt=m.get("type")==null?null:String.valueOf(m.get("type"));
			String var=m.containsKey("varName")?String.valueOf(m.get("varName")):("var"+(int)(Math.random()*1_000_000));
			en.addProperty(new Property(pn,pt==null?null:PropertyType.valueOf(pt)),var);
		}
		List<?>rmPropsRaw=(List<?>)body.getOrDefault("removeProperties",List.of());
		for(Object o:rmPropsRaw){
			String pn=String.valueOf(o);
			en.properties.stream().filter(p->p.getName().equals(pn)).findAny().ifPresent(en::removeProperty);
		}
		List<?>chgPropsRaw=(List<?>)body.getOrDefault("changeProperties",List.of());
		for(Object o:chgPropsRaw)if(o instanceof Map<?,?> m){
			String pn=String.valueOf(m.get("name"));
			EditableNode.Property prop=en.properties.stream().filter(p->p.getName().equals(pn)).findAny().orElse(null);
			if(prop==null)continue;
			String nn=(String)m.get("newName");
			String nt=(String)m.get("type");
			if(nn!=null&&!nn.isBlank())prop.changeName(nn,en);
			if(nt!=null&&!nt.isBlank())prop.changeType(PropertyType.valueOf(nt),en);
		}
		return Map.of("status","ok");
	}

	@GET
	@Path("/permissions")
	public List<Map<String,Object>>permissions(@QueryParam("detailed") boolean detailed){
		PermissionsNode pn=(PermissionsNode)graph.nodes.stream().filter(n->n instanceof PermissionsNode).findAny().orElse(null);
		if(pn==null)return List.of();
		List<Map<String,Object>>out=new ArrayList<>();
		File loc=pn.location==null?null:pn.location;
		for(String perm:pn.permissions){
			Map<String,Object>m=new HashMap<>();
			m.put("permission",perm);
			m.put("location",loc==null?null:loc.getPath());
			if(detailed){} // no extra data for now
			out.add(m);
		}
		return out;
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
	public List<Map<String,Object>>roles(@QueryParam("detailed") boolean detailed){
		RolesNode rn=(RolesNode)graph.nodes.stream().filter(n->n instanceof RolesNode).findAny().orElse(null);
		if(rn==null)return List.of();
		File loc=rn.location==null?null:rn.location;
		List<Map<String,Object>>out=new ArrayList<>();
		for(RoleRepresentation r:rn.roles){
			Map<String,Object>m=new HashMap<>();
			m.put("name",r.name);
			m.put("location",loc==null?null:loc.getPath());
			if(detailed)m.put("permissions",r.permissions==null?List.of():new ArrayList<>(r.permissions));
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
	public List<Map<String,Object>>navigator(@QueryParam("detailed") boolean detailed){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)return List.of();
		File loc=nn.location==null?null:nn.location;
		List<Map<String,Object>>out=new ArrayList<>();
		for(HelpEntry e:nn.entries){
			Map<String,Object>m=new HashMap<>();
			m.put("text",e.text);
			m.put("location",loc==null?null:loc.getPath());
			if(detailed){
				List<Map<String,Object>>ins=new ArrayList<>();
				for(Instruction i:e.instructions){
					Map<String,Object>im=new HashMap<>();
					im.put("type",i.type.name());
					im.put("text",i.text);
					ins.add(im);
				}
				m.put("instructions",ins);
			}
			out.add(m);
		}
		return out;
	}

	@POST
	@Path("/navigator")
	public Map<String,Object>ensureNavigator(){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null){
			nn=new NavigatorNode(graph.projectFolder);
			graph.nodes.add(nn);
		}
		return Map.of("status","ok");
	}

	@PUT
	@Path("/navigator/entries/{text}")
	public Map<String,Object>navigatorChangeText(@PathParam("text")String text,Map<String,String>body){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)throw new IllegalStateException("NavigatorNode not found");
		String newText=body.get("text");
		NavigatorNode.HelpEntry e=nn.entries.stream().filter(x->x.text.equals(text)).findAny().orElse(null);
		if(e==null)throw new IllegalArgumentException("Entry not found: "+text);
		e.changeText(newText,nn);
		return Map.of("status","ok");
	}

	@POST
	@Path("/navigator/entries/{text}/instructions")
	public Map<String,Object>navigatorAppendInstruction(@PathParam("text")String text,Map<String,String>body){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)throw new IllegalStateException("NavigatorNode not found");
		NavigatorNode.HelpEntry e=nn.entries.stream().filter(x->x.text.equals(text)).findAny().orElse(null);
		if(e==null)throw new IllegalArgumentException("Entry not found: "+text);
		Instruction.Type type=Instruction.Type.valueOf(body.getOrDefault("type","TEXT"));
		String iText=body.get("text");
		e.appendInstruction(new Instruction(iText,type),nn);
		return Map.of("status","ok");
	}
	
	@PUT
	@Path("/navigator/entries/{text}/instructions/{index}")
	public Map<String,Object>navigatorReplaceInstruction(@PathParam("text")String text,@PathParam("index")int index,Map<String,String>body){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)throw new IllegalStateException("NavigatorNode not found");
		NavigatorNode.HelpEntry e=nn.entries.stream().filter(x->x.text.equals(text)).findAny().orElse(null);
		if(e==null)throw new IllegalArgumentException("Entry not found: "+text);
		Instruction.Type type=Instruction.Type.valueOf(body.getOrDefault("type","TEXT"));
		String iText=body.get("text");
		e.replaceInstruction(new Instruction(iText,type),index,nn);
		return Map.of("status","ok");
	}
	
	@DELETE
	@Path("/navigator/entries/{text}/instructions/last")
	public Map<String,Object>navigatorDeleteLastInstruction(@PathParam("text")String text){
		NavigatorNode nn=(NavigatorNode)graph.nodes.stream().filter(n->n instanceof NavigatorNode).findAny().orElse(null);
		if(nn==null)throw new IllegalStateException("NavigatorNode not found");
		NavigatorNode.HelpEntry e=nn.entries.stream().filter(x->x.text.equals(text)).findAny().orElse(null);
		if(e==null)throw new IllegalArgumentException("Entry not found: "+text);
		e.deleteLastInstruction(nn);
		return Map.of("status","ok");
	}
	
	@GET
	@Path("/features")
	public List<Map<String,Object>>features(@QueryParam("detailed") boolean detailed){
		return graph.nodes.stream().filter(n->n instanceof FeatureConfigNode).map(n->{
			Map<String,Object>m=new HashMap<>();
			m.put("name",((FeatureConfigNode)n).getFeatureName());
			m.put("location",n.location==null?null:n.location.getPath());
			if(detailed)m.put("type",n.getClass().getSimpleName());
			return m;
		}).toList();
	}
	/* @POST
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
	} */
	
	@POST
	@Path("/reload")
	public Map<String,Object>reload(){
		ProjectGraph ng=new ProjectGraph(graph.projectFolder);
		graph.nodes=ng.nodes;
		return Map.of("status","ok","counts",graph.nodes.stream().collect(Collectors.groupingBy(n->n.getClass().getSimpleName(),Collectors.counting())));
	}
	
	@GET
	@Path("/impl-features")
	public List<Map<String,Object>>implFeatures(@QueryParam("detailed") boolean detailed){
		return graph.nodes.stream().filter(n->n instanceof FeatureNode).map(n->{
			Map<String,Object>m=new HashMap<>();
			m.put("name",((FeatureNode)n).name);
			m.put("location",n.location==null?null:n.location.getPath());
			if(detailed)m.put("type",n.getClass().getSimpleName());
			return m;
		}).toList();
	}

	//Raw access
	@GET
	@Path("/raw/node/{node-index}")
	public Map<String,Object>nodeSummary(@PathParam("node-index") int index){
		ProjectNode n=graph.nodes.get(index);
		Map<String,Object>m=new HashMap<>();
		m.put("type",n.getClass().getSimpleName());
		m.put("path",n.location==null?null:n.location.getPath());
		return m;
	}
}
