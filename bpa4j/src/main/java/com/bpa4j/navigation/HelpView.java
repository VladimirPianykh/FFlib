package com.bpa4j.navigation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import com.bpa4j.Wrapper;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;

/**
 * <p>Displays Instruction-by Instruction guide for reaching some functions.</p>
 * <p>Guides are parsed from the {@code helppath.cfg} file.</p>
 * <p>
 * Each line of the file should consist of <i>path</i> and description, separeted with a space.
 * Path contains elements, delimited with a dot.
 * Each element starts with a <i>flag</i> and continues with some text (it depends on the flag choosen).
 * </p>
 * <p>
 * There are 4 flags with the corresponding meaning:
 * <ul>
 * <li>s - "Log in with the designated user."</li>
 * <li>f - "Select the given feature."</li>
 * <li>t - any text</li>
 * <li>c - any darkened italic text</li>
 * </ul>
 * Use underlines instead of spaces and semicolons instead of dots after the flag.
 */
public final class HelpView{
	public static interface Instruction{
		char getType();
		String toString();
	}
	public static class StartInstruction implements Instruction{
		private final User user;
		public StartInstruction(User user){
			this.user=user;
		}
		public char getType(){
			return 's';
		}
		public final String toString(){
			return ProgramStarter.authRequired?"Введите логин и пароль: \""+user.login+"\","+user.password+".":"Выберите пользователя \""+user.login+"\".";
		}
	}
	public static class FeatureInstruction implements Instruction{
		private final Feature<?> feature;
		public FeatureInstruction(Feature<?> feature){
			this.feature=feature;
		}
		public char getType(){
			return 'f';
		}
		public String toString(){
			return "Перейдите на вкладку \""+feature.toString()+"\".";
		}
	}
	public static class TextInstruction implements Instruction{
		public TextInstruction(String text){
			this.text=text;
		}
		private final String text;
		public char getType(){
			return 't';
		}
		public String toString(){
			return text;
		}
	}
	public static class CommentInstruction implements Instruction{
		private final String text;
		public CommentInstruction(String text){
			this.text=text;
		}
		public char getType(){
			return 'c';
		}
		public String toString(){
			return text;
		}
	}
	public static class ErrorInstruction implements Instruction{
		public ErrorInstruction(){}
		public char getType(){
			return 'e';
		}
		public String toString(){
			return "<навигатор потерялся>";
		}
	}
	private HelpView(){}
	public static ArrayList<ImplementedInfo>paths=new ArrayList<>();
	static{
		loadNavigationFromTaskLocs();
		loadNavigationFromFile();
	}
	public static List<Instruction>parse(String line){
		ArrayList<Instruction>list=new ArrayList<>();
		for(String in:line.split("\\.")){
			String[]s=in.split(" ",2);
			char type=s[0].charAt(0);
			String text=in.length()==1?"":s[1];
			switch(type){
				case 's'->{
					Wrapper<User>w=new Wrapper<>(null);
					User.forEachUser(u->{if(u.login.equalsIgnoreCase(text.replace('_',' ')))w.var=u;});
					if(w.var==null)throw new IllegalArgumentException("There is no user with login \""+text+"\".");
					list.add(new StartInstruction(w.var));
				}
				case 'f'->{
					boolean found = false;
					for(Feature<?>f:User.registeredFeatures)if(f.getName().replace(' ','_').equalsIgnoreCase(text)){
						list.add(new FeatureInstruction(f));
						found = true;
						break;
					}
					if(!found)throw new IllegalArgumentException("There is no feature with text \""+text+"\".");
				}
				case 't'->list.add(new TextInstruction(text.replace('_',' ').replace(';','.')));
				case 'c'->list.add(new CommentInstruction(text.replace('_',' ').replace(';','.')));
				default->list.add(new ErrorInstruction());
			}
		}
		return list;
	}
	public static void show(ImplementedInfo info){
		Navigator.renderer.renderInfo(info);
	}
	/*public static void showIfFirst(ImplementedInfo info){
		File f=new File(Root.folder+"ttentry/"+info);
		if(!f.exists())try{
			f.createNewFile();
			show(info);
		}catch(IOException ex){throw new UncheckedIOException(ex);}
	}*/
	public static void loadNavigationFromTaskLocs(){
		HashSet<ImplementedInfo>s=new HashSet<>();
		for(Role r:User.registeredRoles)s.addAll(r.getImplementedInfo());
		paths.addAll(s);
	}
	public static void loadNavigationFromFile(){
		try(Scanner sc=new Scanner(Root.getResourceAsStream("resources/helppath.cfg"))){
			while(sc.hasNextLine()){
				String[]s=sc.nextLine().split(" ",2);
				paths.add(new ImplementedInfo(s[1],parse(s[0])));
			}
		}catch(NullPointerException ex){
			System.err.println("Navigation file (helppath.cfg) not found.");
		}
	}
}
