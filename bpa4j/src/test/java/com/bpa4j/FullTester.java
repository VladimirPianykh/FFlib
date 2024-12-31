package com.bpa4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import com.bpa4j.core.Navigator;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Registrator;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.core.User.Feature;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.defaults.editables.AbstractCustomer;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.defaults.features.Board;
import com.bpa4j.defaults.features.Calendar;
import com.bpa4j.defaults.features.DefaultFeature;
import com.bpa4j.defaults.features.Report;
import com.bpa4j.defaults.ftr_attributes.data_renderers.AnswerDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.ChartDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.TableDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.ChartDataRenderer.ChartMode;
import com.bpa4j.defaults.ftr_attributes.daters.EventDater;
import com.bpa4j.defaults.ftr_attributes.element_suppliers.ChartDataConverter;
import com.bpa4j.defaults.ftr_attributes.element_suppliers.GroupElementSupplier;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.editor.modules.CustomerModule;
import com.bpa4j.editor.modules.ExcludeModule;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.editor.modules.LimitToModule;
import com.bpa4j.editor.modules.LogWatchModule;
import com.bpa4j.editor.modules.StageApprovalModule;
import com.bpa4j.util.TestGen;

public class FullTester{
	public static class MyFourthEditable extends Editable{
		static int index=2024;
		@EditorEntry(translation="Группа")
		public String group=new String[]{"С баллом <100","С баллом <200"}[(int)(Math.random()*2)];
		@EditorEntry(translation="Категория")
		public String value1=String.valueOf(index--);
		@EditorEntry(translation="Значение")
		public int value2=(int)(Math.random()*100);
		public MyFourthEditable(){super("Новый объект");}
	}
	public static class MyThirdEditable extends Editable{
		@EditorEntry(translation="Группа")
		public String group=new String[]{"С баллом <100","С баллом <200"}[(int)(Math.random()*2)];
		@EditorEntry(translation="Величина 1")
		public int value1=(int)(Math.random()*100);
		@EditorEntry(translation="Величина 2")
		public int value2=(int)(Math.random()*100);
		public MyThirdEditable(){super("Новый объект");}
	}
	public static class MyCustomer extends AbstractCustomer{}
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
		public Status a=Status.S1;
		public MySecondEditable(){
			super("Новый объект");
		}
	}
	public static class MyProcessable extends Processable{
		@EditorEntry(translation="Строка")
		public String strField;
		@EditorEntry(translation="Число")
		public int intField;
		@EditorEntry(translation="Дробное число")
		public double doubleField;
		@EditorEntry(translation="Дата")
		public LocalDate dateField=LocalDate.now();
		@EditorEntry(translation="Редактируемый объект")
		public MySecondEditable editableField;
		@EditorEntry(translation="Supplier")
		public static Supplier<String>s=()->Stream.generate(()->(char)('a'+Math.random()*25)).limit(20).map(c->String.valueOf(c)).reduce((c1,c2)->c1+c2).get();
		public MyProcessable(){
			super(
				"Новый объект",
				new Stage("Отрицание",AppPermission.MANAGE_PROCESSABLE,()->{
					Wrapper<Boolean>w=new Wrapper<Boolean>(true);
					ProgramStarter.editor.constructEditor(new MySecondEditable(),true,()->w.var=false);
					return w.var;
				}),
				new Stage("Гнев",AppPermission.MANAGE_PROCESSABLE,AppPermission.MANAGE_PROCESSABLE,0),
				new Stage("Торг",AppPermission.MANAGE_PROCESSABLE,AppPermission.MANAGE_PROCESSABLE,0),
				new Stage("Депрессия",AppPermission.MANAGE_PROCESSABLE,AppPermission.MANAGE_PROCESSABLE,0),
				new Stage("Принятие",AppPermission.MANAGE_PROCESSABLE,AppPermission.MANAGE_PROCESSABLE,0)
			);
		}
	}
	public static class MyEvent implements Calendar.Event{
		@EditorEntry(translation="Название")
		public String property1="НТО";
		@EditorEntry(translation="Этап")
		public String property2=new String[]{"1 отборочный","2 отборочный","Финал"}[(int)(Math.random()*3)];
		public String toString(){return "Олимпиада";}
	}
	public enum AppRole implements Role{
		MYROLE(
			()->AppPermission.values(),
			()->new Feature[]{
				DefaultFeature.MODEL_EDITING,
				Board.registerBoard("board",MyProcessable.class),
				Calendar.registerCalendar("calendar",MyEvent.class),
				Report.registerReport("report")
			}
		);
		private AppRole(Supplier<Permission[]>p,Supplier<Feature[]>f){SwingUtilities.invokeLater(()->Registrator.register(this,f.get(),p.get()));}
	}
	public enum AppPermission implements Permission{
		READ_MYPROCESSABLE,
		CREATE_MYPROCESSABLE,
		READ_MYTHIRDEDITABLE,
		CREATE_MYTHIRDEDITABLE,
		READ_MYFOURTHEDITABLE,
		CREATE_MYFOURTHEDITABLE,
		READ_MYCUSTOMER,
		CREATE_MYCUSTOMER,
		MANAGE_PROCESSABLE;
		private AppPermission(){Registrator.register(this);}
	}
	public static void main(String[]args){
		Navigator.init();
		ProgramStarter.welcomeMessage="";
		ProgramStarter.authRequired=false;
		ProgramStarter.editor=new ModularEditor(
			new LimitToModule(new StageApprovalModule(),MyProcessable.class),
			new LimitToModule(new LogWatchModule(),MyProcessable.class),
			new LimitToModule(new CustomerModule(),MyCustomer.class),
			new ExcludeModule(new FormModule(),MyProcessable.class,MyCustomer.class)
		);
		if(ProgramStarter.isFirstLaunch()){
			User.register("Пользователь 1","",AppRole.MYROLE);
			EditableGroup<MyProcessable>myProcessables=new EditableGroup<MyProcessable>(
				new PathIcon("ui/left.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				new PathIcon("ui/right.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				MyProcessable.class
			);
			EditableGroup<MyThirdEditable>myThirdEditables=new EditableGroup<MyThirdEditable>(
				new PathIcon("ui/left.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				new PathIcon("ui/right.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				MyThirdEditable.class
			);
			EditableGroup<MyFourthEditable>myFourthEditables=new EditableGroup<MyFourthEditable>(
				new PathIcon("ui/left.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				new PathIcon("ui/right.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				MyFourthEditable.class
			);
			EditableGroup<MyCustomer>customers=new EditableGroup<MyCustomer>(
				new PathIcon("ui/left.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				new PathIcon("ui/right.png",Root.SCREEN_SIZE.height/11,Root.SCREEN_SIZE.height/11),
				MyCustomer.class
			);
			Registrator.register(myProcessables);
			Registrator.register(myThirdEditables.hide());
			Registrator.register(myFourthEditables.hide());
			Registrator.register(customers);
			ProgramStarter.runProgram();
			TestGen.generate(4,myProcessables);
			TestGen.generate(50,myThirdEditables,myFourthEditables);
		}else ProgramStarter.runProgram();
		GroupElementSupplier<MyProcessable>groupES=new GroupElementSupplier<>(MyProcessable.class);
		Board.<MyProcessable>getBoard("board").setSorter(new Board.Sorter<MyProcessable>(){
			private final JComboBox<Boolean>c=new JComboBox<>();
			public JComponent getConfigurator(Runnable saver,ArrayList<MyProcessable>objects){
				c.removeAllItems();
				c.addItem(true);
				c.addItem(false);
				c.addItemListener(e->saver.run());
				return c;
			}
			public int compare(MyProcessable o1,MyProcessable o2){
				return(c.getSelectedItem()==null||(boolean)c.getSelectedItem())?o1.name.length()-o2.name.length():o1.strField.length()-o2.strField.length();
			}
		}).setElementSupplier(groupES).setAllowCreation(true);
		Calendar.<MyEvent>getCalendar("calendar")
			.setEventFiller(m->{
				for(int i=0;i<10;++i){
					LocalDate t=LocalDate.now().plusDays((int)(Math.random()*15));
					if(m.containsKey(t))m.get(t).add(new MyEvent());
					else m.put(t,new ArrayList<>(Stream.of(new MyEvent()).toList()));
				}
			})
			.setDater(new EventDater<>(EventDater::listProperties));
		JSpinner s=new JSpinner(new SpinnerNumberModel(50,10,100,1));
		Report.getReport("report")
			.addConfigurator(saver->{
				s.addChangeListener(e->saver.run());
				return s;
			}).addDataRenderer(new ChartDataRenderer(
				ChartMode.LINEAR_COMPARE,
				new ChartDataConverter<>(()->new ArrayList<>(new GroupElementSupplier<>(MyThirdEditable.class).get().stream().filter(e->e.value1<(int)s.getValue()).toList())),
				"Координаты смертей после резов",
				"x",
				"y"
			)).addDataRenderer(new ChartDataRenderer(
				ChartMode.BAR,
				new ChartDataConverter<>(new GroupElementSupplier<>(MyFourthEditable.class)),
				"Смерти после резов",
				"Год",
				"Количество, млн. чел"
			)).addDataRenderer(new AnswerDataRenderer(
				()->"ЧЕКЛИСТ НТО.",
				()->"Проблемы: есть",
				()->"Паника: есть",
				()->"Кровь: "+((int)s.getValue()>50?"есть "+((int)s.getValue()-50)+" литров":"нет (но это временно)")
			))
			.addDataRenderer(new TableDataRenderer<>(groupES,"Данные о жертвах НТО",true));
	}
}
