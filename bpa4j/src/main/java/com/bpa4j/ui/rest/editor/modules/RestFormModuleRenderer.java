package com.bpa4j.ui.rest.editor.modules;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.defaults.input.ConditionalWEditor;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.defaults.input.FlagWEditor;
import com.bpa4j.defaults.input.FunctionEditor;
import com.bpa4j.defaults.input.SelectFromEditor;
import com.bpa4j.defaults.input.SelectionListEditor;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.RestTheme;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.components.ComboBox;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.components.complex.DatePicker;
import com.bpa4j.ui.rest.abstractui.components.complex.DateTimePicker;
import com.bpa4j.ui.rest.abstractui.components.complex.TimePicker;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.editor.RestConditionalWEditorRenderer;
import com.bpa4j.ui.rest.editor.RestFlagWEditorRenderer;
import com.bpa4j.ui.rest.editor.RestFunctionEditorRenderer;
import com.bpa4j.ui.rest.editor.RestSelectFromEditorRenderer;
import com.bpa4j.ui.rest.editor.RestSelectionListEditorRenderer;

/**
 * @author AI-generated
 */
public class RestFormModuleRenderer implements ModuleRenderer<FormModule>{
	// Renderer registry for EditorEntryBase implementations
	private static final HashMap<Class<? extends EditorEntryBase>,EditorEntryBaseRenderer> editorRenderers=new HashMap<>();
	static{
		editorRenderers.put(FlagWEditor.class,new RestFlagWEditorRenderer());
		editorRenderers.put(ConditionalWEditor.class,new RestConditionalWEditorRenderer());
		editorRenderers.put(FunctionEditor.class,new RestFunctionEditorRenderer());
		editorRenderers.put(SelectFromEditor.class,new RestSelectFromEditorRenderer());
		editorRenderers.put(SelectionListEditor.class,new RestSelectionListEditorRenderer());
	}
	// EditorEntryRenderingContext implementation
	public static class RestEditorEntryRenderingContext implements EditorEntryBase.EditorEntryRenderingContext{
		private Panel target;
		public RestEditorEntryRenderingContext(Panel target){
			this.target=target;
		}
		public EditorEntryBaseRenderer getRenderer(EditorEntryBase base){
			EditorEntryBaseRenderer renderer=editorRenderers.get(base.getClass());
			if(renderer==null) throw new IllegalStateException("No renderer registered for "+base.getClass());
			return renderer;
		}
		public void addComponent(Component c){
			target.add(c);
		}
	}
	// Defensive check configuration - set to true to throw exceptions, false to use fallback behavior
	private static final boolean THROW_ON_NULL_EDITABLE=true;
	private static final boolean THROW_ON_NULL_CONTEXT=true;
	private static final boolean THROW_ON_NULL_SAVER=true;
	private static final boolean THROW_ON_FIELD_ACCESS_ERROR=true;
	private static void decorateOkButton(Button okButton,boolean done){
		okButton.setText(done?"Done":"Next");
		okButton.setBackground(done?RestTheme.ACCENT:RestTheme.MAIN);
		okButton.setForeground(done?RestTheme.ON_ACCENT:RestTheme.ACCENT_TEXT);
	}
	public void createTab(Editable editable,boolean isNew,Runnable deleter,FormModule module,ModulesRenderingContext ctx){
		// Defensive checks
		if(editable==null){
			if(THROW_ON_NULL_EDITABLE) throw new IllegalArgumentException("Editable cannot be null");
			return; // Fallback: do nothing
		}
		if(ctx==null){
			if(THROW_ON_NULL_CONTEXT) throw new IllegalArgumentException("ModulesRenderingContext cannot be null");
			return; // Fallback: do nothing
		}

		RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
		Panel container=rctx.getTarget();
		if(container==null){
			if(THROW_ON_NULL_CONTEXT) throw new IllegalStateException("Target panel cannot be null");
			return; // Fallback: do nothing
		}

		container.removeAll();
		container.setLayout(new com.bpa4j.ui.rest.abstractui.layout.BorderLayout(10,10));

		// Name Panel (North)
		Panel namePanel=new Panel(new com.bpa4j.ui.rest.abstractui.layout.BorderLayout(5,5));
		namePanel.setSize(container.getWidth(),40);

		Label nameLabel=new Label("Name");
		nameLabel.setSize(50,40);
		namePanel.add(nameLabel);

		// Close Button (Right aligned in header)
		Button deleteBtn=null;
		if(deleter!=null){
			deleteBtn=new Button("✕");
			deleteBtn.setSize(40,40);
			deleteBtn.setBackground(RestTheme.DANGER);
			deleteBtn.setForeground(RestTheme.ON_DANGER);
			deleteBtn.setOnClick(b->{
				deleter.run();
				rctx.close();
			});
			namePanel.add(deleteBtn);
		}

		TextField nameField=new TextField(editable.name!=null?editable.name:"");
		// Size will be managed by BorderLayout Center
		namePanel.add(nameField);

		com.bpa4j.ui.rest.abstractui.layout.BorderLayout nameLayout=(com.bpa4j.ui.rest.abstractui.layout.BorderLayout)namePanel.getLayout();
		nameLayout.addLayoutComponent(nameLabel,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.WEST);
		if(deleteBtn!=null) nameLayout.addLayoutComponent(deleteBtn,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.EAST);
		nameLayout.addLayoutComponent(nameField,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.CENTER);

		// Form Panel (Center) - using GridLayout for horizontal label-editor layout
		Panel formPanel=new Panel(new GridLayout(1,2,10,10));
		formPanel.setSize(container.getWidth(),container.getHeight()-100);

		// Button Panel (South) - vertical layout with back above OK
		Panel buttonPanel=new Panel(new GridLayout(!isNew?3:2,1,5,5));
		buttonPanel.setSize(container.getWidth(),80);

		// Collect fields and components
		List<Field> fields=new ArrayList<>();
		List<Supplier<?>> savers=new ArrayList<>();
		List<Component> components=new ArrayList<>();
		List<String> labels=new ArrayList<>();

		for(Field f:editable.getClass().getFields())
			try{
				EditorEntry a=f.getAnnotation(EditorEntry.class);
				if(a==null) continue;

				boolean hide=false,readonly=false;
				for(String p:a.properties()){ //Handling entry flags
					if("hide".equals(p)) hide=true;
					if("readonly".equals(p)||(!isNew&&"initonly".equals(p))) readonly=true;
				}
				if(hide) continue;

				fields.add(f);
				labels.add(a.translation()!=null?a.translation():"");

				if(readonly){
					Object value=f.get(editable);
					Label vLabel=new Label(value==null?"":String.valueOf(value));
					vLabel.setForeground(RestTheme.MAIN_TEXT);
					components.add(vLabel);
					savers.add(new EmptySaver());
					continue;
				}

				Component editorComponent;
				if(a.editorBaseSource()==EditorEntryBase.class){
					Wrapper<Supplier<?>> nextSaver=new Wrapper<>(null);
					editorComponent=createEditorComponent(editable,f,nextSaver);
					if(nextSaver.var==null){
						if(THROW_ON_NULL_SAVER) throw new IllegalStateException("Saver not set for field: "+f.getName());
						nextSaver.var=new EmptySaver(); // Fallback
					}
					savers.add(nextSaver.var);
				}else{
					EditorEntryBase editorBase=a.editorBaseSource().getDeclaredConstructor().newInstance();
					Panel target=new Panel();
					target.setLayout(new com.bpa4j.ui.rest.abstractui.layout.FlowLayout(com.bpa4j.ui.rest.abstractui.layout.FlowLayout.LEFT,com.bpa4j.ui.rest.abstractui.layout.FlowLayout.LTR,5,5));
					EditorEntryBase.EditorEntryRenderingContext renderingContext=new RestEditorEntryRenderingContext(target);
					Wrapper<Supplier<?>> saver=new Wrapper<>(null);
					Wrapper<EditableDemo> demo=new Wrapper<>(null);
					editorBase.renderEditorBase(editable,f,saver,demo,renderingContext);
					editorComponent=target.getComponents().isEmpty()?null:target;
					if(saver.var==null){
						if(THROW_ON_NULL_SAVER) throw new IllegalStateException("All entry editors must set a saver for field: "+f.getName());
						saver.var=new EmptySaver(); // Fallback
					}
					savers.add(saver.var);
				}
				components.add(editorComponent);

			}catch(Exception ex){
				if(THROW_ON_FIELD_ACCESS_ERROR) throw new IllegalStateException("Error processing field: "+f.getName(),ex);
				// Fallback: skip this field and continue
			}

		AtomicInteger currentIndex=new AtomicInteger(0);
		Button back=new Button("Back");
		back.setBackground(RestTheme.MAIN);
		Button ok=new Button();
		decorateOkButton(ok,false);
		Runnable updateUI=()->{
			formPanel.removeAll();
			if(fields.isEmpty()){
				Label l=new Label("No fields to edit.");
				l.setForeground(RestTheme.MAIN_TEXT);
				formPanel.add(l);
				back.setEnabled(false);
				decorateOkButton(ok,true);
				return;
			}

			int idx=currentIndex.get();
			// Show label and component in horizontal GridLayout
			Label l=new Label(labels.get(idx));
			formPanel.add(l);

			Component c=components.get(idx);
			if(c!=null){
				formPanel.add(c);
			}else{
				formPanel.add(new Label("")); // Empty placeholder for grid layout
			}

			back.setEnabled(idx>0);
			decorateOkButton(ok,idx==fields.size()-1);

			formPanel.update();
		};
		back.setOnClick(b->{
			int idx=currentIndex.get();
			if(idx>0){
				currentIndex.decrementAndGet();
				updateUI.run();
			}
		});
		ok.setOnClick(b->{
			try{
				if(!fields.isEmpty()){
					int idx=currentIndex.get();
					if(idx>=0&&idx<savers.size()){ // Defensive check
						Supplier<?> saver=savers.get(idx);
						if(saver!=null&&!(saver instanceof EmptySaver)){
							Object savedValue=saver.get();
							fields.get(idx).set(editable,savedValue);
						}
					}
				}

				if(currentIndex.get()<fields.size()-1){
					currentIndex.incrementAndGet();
					updateUI.run();
				}else{
					String name=nameField.getText();
					editable.name=name!=null?name:"";
					rctx.close();
				}
			}catch(Exception ex){
				if(THROW_ON_FIELD_ACCESS_ERROR) throw new IllegalStateException("Error saving field value",ex);
				// Fallback: continue anyway
			}
		});

		// Add buttons in vertical order: back above OK
		if(!isNew){
			Button cancel=new Button("Cancel");
			cancel.setBackground(RestTheme.MAIN);
			cancel.setForeground(RestTheme.DANGER);
			cancel.setOnClick(b->rctx.close());
			buttonPanel.add(cancel);
		}
		buttonPanel.add(back);
		buttonPanel.add(ok);

		com.bpa4j.ui.rest.abstractui.layout.BorderLayout layout=(com.bpa4j.ui.rest.abstractui.layout.BorderLayout)container.getLayout();
		layout.addLayoutComponent(namePanel,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.NORTH);
		layout.addLayoutComponent(formPanel,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.CENTER);
		layout.addLayoutComponent(buttonPanel,com.bpa4j.ui.rest.abstractui.layout.BorderLayout.SOUTH);

		container.add(namePanel);
		container.add(formPanel);
		container.add(buttonPanel);

		updateUI.run();
		// Finalize this tab so next module gets a new one
		rctx.finalizeCurrentTab();
	}

	@SuppressWarnings("unchecked")
	public static Component createEditorComponent(Object o,Field f,Wrapper<Supplier<?>> saver) throws IllegalAccessException{
		Class<?> type=f.getType();

		// String
		if(type==String.class){
			String value=(String)f.get(o);
			TextField tf=new TextField(value);
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->tf.getText();
			return tf;
		}

		// boolean/Boolean
		else if(type==boolean.class||type==Boolean.class){
			boolean value=type==boolean.class?f.getBoolean(o):(Boolean)f.get(o);
			CheckBox cb=new CheckBox();
			cb.setSelected(value);
			saver.var=()->cb.isSelected();
			return cb;
		}

		// int
		else if(type==int.class){
			int value=f.getInt(o);
			TextField tf=new TextField(String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?0:Integer.parseInt(txt);
				}catch(NumberFormatException e){
					return 0;
				}
			};
			return tf;
		}

		// Integer
		else if(type==Integer.class){
			Integer value=(Integer)f.get(o);
			TextField tf=new TextField(value==null?"":String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?null:Integer.parseInt(txt);
				}catch(NumberFormatException e){
					return null;
				}
			};
			return tf;
		}

		// double
		else if(type==double.class){
			double value=f.getDouble(o);
			TextField tf=new TextField(String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?0d:Double.parseDouble(txt);
				}catch(NumberFormatException e){
					return 0d;
				}
			};
			return tf;
		}

		// Double
		else if(type==Double.class){
			Double value=(Double)f.get(o);
			TextField tf=new TextField(value==null?"":String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?null:Double.parseDouble(txt);
				}catch(NumberFormatException e){
					return null;
				}
			};
			return tf;
		}

		// float
		else if(type==float.class){
			float value=f.getFloat(o);
			TextField tf=new TextField(String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?0f:Float.parseFloat(txt);
				}catch(NumberFormatException e){
					return 0f;
				}
			};
			return tf;
		}

		// Float
		else if(type==Float.class){
			Float value=(Float)f.get(o);
			TextField tf=new TextField(value==null?"":String.valueOf(value));
			tf.setForeground(RestTheme.MAIN_TEXT);
			saver.var=()->{
				String txt=tf.getText();
				try{
					return txt.isBlank()?null:Float.parseFloat(txt);
				}catch(NumberFormatException e){
					return null;
				}
			};
			return tf;
		}

		// LocalDate - use DatePicker
		else if(type==LocalDate.class){
			LocalDate value=(LocalDate)f.get(o);
			DatePicker dp=new DatePicker(value!=null?value:LocalDate.now());
			saver.var=()->dp.getDate();
			return dp;
		}

		// LocalTime - use TimePicker
		else if(type==LocalTime.class){
			LocalTime value=(LocalTime)f.get(o);
			TimePicker tp=new TimePicker(value!=null?value:LocalTime.now());
			saver.var=()->tp.getTime();
			return tp;
		}

		// LocalDateTime - use DateTimePicker
		else if(type==LocalDateTime.class){
			LocalDateTime value=(LocalDateTime)f.get(o);
			DateTimePicker dtp=new DateTimePicker(value!=null?value:LocalDateTime.now());
			saver.var=()->dtp.getDateTime();
			return dtp;
		}

		// Editable subclasses - use ComboBox if registered, Button otherwise
		else if(Editable.class.isAssignableFrom(type)){
			try{
				// Try to get registered group - if successful, use ComboBox
				ComboBox cb=new ComboBox();
				EditableGroup<? extends Editable> group=ProgramStarter.getStorageManager().getStorage().getGroup((Class<? extends Editable>)type);
				ArrayList<String> items=new ArrayList<>();
				ArrayList<Editable> editables=new ArrayList<>();
				for(Editable e:group){
					items.add(e.name);
					editables.add(e);
				}
				cb.setItems(items);

				Editable currentVal=(Editable)f.get(o);
				if(currentVal!=null){
					int index=editables.indexOf(currentVal);
					if(index>=0) cb.setSelectedIndex(index);
				}

				saver.var=()->{
					int idx=cb.getSelectedIndex();
					if(idx>=0&&idx<editables.size()) return editables.get(idx);
					return currentVal; // Return current value if nothing selected
				};
				return cb;
			}catch(IllegalArgumentException exception){
				// Group not registered - use Button to open editor
				Wrapper<Editable> value=new Wrapper<>((Editable)f.get(o));
				Button a=new Button(value.var==null?"":value.var.name);

				// Action for editing existing object
				Runnable edit=()->{
					if(value.var!=null){
						ProgramStarter.editor.constructEditor(value.var,false,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					}
				};

				// If value is null, first click creates new object
				if(value.var==null){
					a.setOnClick(btn->{
						try{
							f.set(o,type.getDeclaredConstructor().newInstance());
							value.var=(Editable)f.get(o);
							ProgramStarter.editor.constructEditor(value.var,true,()->{
								try{
									f.set(o,null);
									value.var=null;
									btn.setText("");
								}catch(IllegalAccessException ex){
									throw new IllegalStateException(ex);
								}
							},ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
							btn.setText(value.var.name);
							btn.setOnClick(b->edit.run());
						}catch(ReflectiveOperationException ex){
							throw new IllegalStateException(ex);
						}
					});
				}else{
					a.setOnClick(btn->edit.run());
				}

				saver.var=()->value.var;
				return a;
			}
		}

		// EditableGroup - display as text field showing count
		else if(EditableGroup.class.isAssignableFrom(type)){
			EditableGroup<?> value=(EditableGroup<?>)f.get(o);
			TextField tf=new TextField(value==null?"":"["+value.size()+" items]");
			tf.setEditable(false); // Make read-only
			saver.var=()->value; // Return the original value unchanged
			return tf;
		}

		// Enum types
		else if(type.isEnum()){
			Object value=f.get(o);
			if(value==null) throw new NullPointerException("Enum value of field \""+f.getName()+"\" is null.");
			TextField tf=new TextField(value.toString());
			saver.var=()->{
				String txt=tf.getText();
				try{
					// Parse enum from string
					@SuppressWarnings("rawtypes")
					Enum en=Enum.valueOf((Class<Enum>)type,txt);
					return en;
				}catch(IllegalArgumentException e){
					return value; // Return original value if parsing fails
				}
			};
			return tf;
		}

		// Unsupported type - throw exception
		throw new UnsupportedOperationException("Component for "+type+" is not defined.");
	}
}
