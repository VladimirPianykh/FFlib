package com.futurefactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Supplier;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.futurefactory.core.Navigator;
import com.futurefactory.core.ProgramStarter;
import com.futurefactory.core.Registrator;
import com.futurefactory.core.Root;
import com.futurefactory.core.User;
import com.futurefactory.core.Data.Editable;
import com.futurefactory.core.Data.EditableGroup;
import com.futurefactory.core.User.Feature;
import com.futurefactory.core.User.Permission;
import com.futurefactory.core.User.Role;
import com.futurefactory.defaults.features.Board;
import com.futurefactory.defaults.features.Calendar;
import com.futurefactory.defaults.features.DefaultFeature;
import com.futurefactory.defaults.ftr_attributes.EmptyDater;
import com.futurefactory.defaults.ftr_attributes.GroupElementSupplier;
import com.futurefactory.editor.EditorEntry;

public class FullTester{
	public static class MySecondEditable extends Editable{
		public static enum Status{
			S1("Состояние 1"),
			S2("Состояние 2"),
			S3("Состояние 3"),
			S4("Состояние 4");
			private String translation;
			private Status(String translation){this.translation=translation;}
			public String toString(){return translation;}
		}
		@EditorEntry(translation="Перечисление")
		public Status a;
		public MySecondEditable(){
			super("Новый объект");
		}
	}
	public static class MyEditable extends Editable{
		@EditorEntry(translation="Строка")
		public String strField;
		@EditorEntry(translation="Число")
		public int intField;
		@EditorEntry(translation="Дата")
		public LocalDate dateField=LocalDate.now();
		@EditorEntry(translation="Редактируемый объект")
		public MySecondEditable editableField;
		public MyEditable(){
			super("Новый объект");
		}
	}
	public static class MyEvent implements Calendar.Event{
		
	}
	public enum AppRole implements Role{
		MYROLE(
			()->AppPermission.values(),
			()->new Feature[]{
				DefaultFeature.MODEL_EDITING,
				Board.registerBoard("board",MyEditable.class),
				Calendar.registerCalendar("calendar",MyEvent.class)
			}
		);
		private AppRole(Supplier<Permission[]>p,Supplier<Feature[]>f){SwingUtilities.invokeLater(()->Registrator.register(this,f.get(),p.get()));}
	}
	public enum AppPermission implements Permission{
		READ_MYEDITABLE,
		CREATE_MYEDITABLE;
		private AppPermission(){Registrator.register(this);}
	}
	public static void main(String[]args){
		Navigator.init();
		ProgramStarter.welcomeMessage="";
		ProgramStarter.authRequired=false;
		if(ProgramStarter.isFirstLaunch()){
			User.register("Пользователь 1","",AppRole.MYROLE);
			EditableGroup<MyEditable>myEditables=new EditableGroup<MyEditable>(
				new PathIcon("ui/left.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				new PathIcon("ui/right.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				MyEditable.class
			);
			Registrator.register(myEditables);
			ProgramStarter.runProgram();
			myEditables.add(new MyEditable());
		}else ProgramStarter.runProgram();
		Board.<MyEditable>getBoard("board").setSorter(new Board.Sorter<MyEditable>(){
			private JComboBox<Boolean>c=new JComboBox<>();
			public JComponent getConfigurator(Runnable saver,ArrayList<MyEditable>objects){
				c.removeAllItems();
				c.addItem(true);
				c.addItem(false);
				c.addItemListener(e->saver.run());
				return c;
			}
			public int compare(MyEditable o1,MyEditable o2){
				return(c.getSelectedItem()==null?true:(boolean)c.getSelectedItem())?o1.name.length()-o2.name.length():o1.strField.length()-o2.strField.length();
			}
		}).setElementSupplier(new GroupElementSupplier<>(MyEditable.class));
		Calendar.<MyEvent>getCalendar("calendar").setDater(new EmptyDater<>());
	}
}
