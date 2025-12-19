package com.bpa4j.ui.rest.features;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.RestTheme;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * Rest renderer for Calendar feature - displays events in a calendar grid
 * @author AI-generated
 */
public class RestCalendarRenderer<T extends Calendar.Event> implements FeatureRenderer<Calendar<T>>{
	private final Calendar<T> contract;
	private YearMonth currentMonth;

	public RestCalendarRenderer(Calendar<T> contract){
		this.contract=contract;
		this.currentMonth=YearMonth.now();
	}

	public Calendar<T> getTransmissionContract(){
		return contract;
	}

	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}

		// Refresh events if event filler is configured
		if(contract.getEventFiller()!=null){
			contract.clearEvents();
			contract.getEventFiller().accept(contract.getEvents());
		}

		// Create root panel with border layout
		Panel root=new Panel(new BorderLayout());
		root.setSize(target.getWidth(),target.getHeight());

		// Create header with navigation
		Panel header=new Panel(new BorderLayout());
		header.setSize(root.getWidth(),60);

		// Navigation buttons
		Button prevButton=new Button("◄ Предыдущий");
		// prevButton.setBackground(RestTheme.MAIN_TEXT);
		// prevButton.setForeground(RestTheme.ACCENT);
		prevButton.setOnClick(btn->{
			currentMonth=currentMonth.minusMonths(1);
			rctx.rebuild();
		});

		Button nextButton=new Button("Следующий ►");
		// nextButton.setBackground(RestTheme.MAIN_TEXT);
		// nextButton.setForeground(RestTheme.ACCENT);
		nextButton.setOnClick(btn->{
			currentMonth=currentMonth.plusMonths(1);
			rctx.rebuild();
		});

		// Month/Year label
		Label monthLabel=new Label(getMonthYearLabel(currentMonth));
		// monthLabel.setForeground(RestTheme.MAIN_TEXT);

		Panel leftNav=new Panel(new FlowLayout());
		leftNav.setSize(150,header.getHeight());
		leftNav.add(prevButton);

		Panel centerTitle=new Panel(new FlowLayout());
		centerTitle.setSize(root.getWidth()-300,header.getHeight());
		centerTitle.add(monthLabel);

		Panel rightNav=new Panel(new FlowLayout());
		rightNav.setSize(150,header.getHeight());
		rightNav.add(nextButton);

		BorderLayout headerLayout=(BorderLayout)header.getLayout();
		headerLayout.addLayoutComponent(leftNav,BorderLayout.WEST);
		headerLayout.addLayoutComponent(centerTitle,BorderLayout.CENTER);
		headerLayout.addLayoutComponent(rightNav,BorderLayout.EAST);
		header.add(leftNav);
		header.add(centerTitle);
		header.add(rightNav);

		// Create weekday headers
		Panel weekdayPanel=new Panel(new GridLayout(1,7,2,2));
		weekdayPanel.setSize(root.getWidth(),30);
		String[] weekdays={"Пн","Вт","Ср","Чт","Пт","Сб","Вс"};
		for(String day:weekdays){
			Label dayLabel=new Label(day);
			// dayLabel.setForeground(RestTheme.MAIN_TEXT);
			weekdayPanel.add(dayLabel);
		}

		// Create calendar grid
		int calendarHeight=root.getHeight()-header.getHeight()-weekdayPanel.getHeight();
		Panel calendarGrid=new Panel(new GridLayout(6,7,2,2)); // 6 rows max for a month
		calendarGrid.setSize(root.getWidth(),calendarHeight);

		fillCalendarGrid(calendarGrid,currentMonth,rctx);

		// Assemble layout
		Panel topSection=new Panel(new BorderLayout());
		topSection.setSize(root.getWidth(),header.getHeight()+weekdayPanel.getHeight());
		BorderLayout topLayout=(BorderLayout)topSection.getLayout();
		topLayout.addLayoutComponent(header,BorderLayout.NORTH);
		topLayout.addLayoutComponent(weekdayPanel,BorderLayout.CENTER);
		topSection.add(header);
		topSection.add(weekdayPanel);

		BorderLayout rootLayout=(BorderLayout)root.getLayout();
		rootLayout.addLayoutComponent(topSection,BorderLayout.NORTH);
		rootLayout.addLayoutComponent(calendarGrid,BorderLayout.CENTER);
		root.add(topSection);
		root.add(calendarGrid);

		target.add(root);
		target.update();
	}

	private void fillCalendarGrid(Panel grid,YearMonth month,RestFeatureRenderingContext rctx){
		grid.removeAll();

		// Get the first day of the month and its day of week (1=Monday, 7=Sunday)
		LocalDate firstDay=month.atDay(1);
		int firstDayOfWeek=firstDay.getDayOfWeek().getValue(); // 1-7

		// Add empty cells for days before the month starts
		for(int i=1;i<firstDayOfWeek;i++){
			Panel emptyCell=new Panel(new FlowLayout());
			Label emptyLabel=new Label("");
			emptyCell.add(emptyLabel);
			grid.add(emptyCell);
		}

		// Add cells for each day of the month
		int daysInMonth=month.lengthOfMonth();
		for(int day=1;day<=daysInMonth;day++){
			LocalDate date=month.atDay(day);
			Panel dayCell=createDayCell(date,rctx);
			grid.add(dayCell);
		}

		// Fill reMAIN_TEXTing cells to complete the grid
		int totalCells=firstDayOfWeek-1+daysInMonth;
		int remainingCells=42-totalCells; // 6 rows * 7 days = 42 cells
		for(int i=0;i<remainingCells;i++){
			Panel emptyCell=new Panel(new FlowLayout());
			Label emptyLabel=new Label("");
			emptyCell.add(emptyLabel);
			grid.add(emptyCell);
		}
	}

	private Panel createDayCell(LocalDate date,RestFeatureRenderingContext rctx){
		Panel cell=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,2,2));

		// Day number label
		Label dayLabel=new Label(String.valueOf(date.getDayOfMonth()));
		dayLabel.setForeground(RestTheme.MAIN_TEXT);
		cell.add(dayLabel);

		// Get events for this date
		List<T> events=contract.getEventList(date);
		if(events!=null&&!events.isEmpty()){
			// Show event count or first few events
			Label eventCount=new Label("События: "+events.size());
			eventCount.setForeground(RestTheme.MAIN_TEXT);
			cell.add(eventCount);

			// If there's a dater configured, use it to render events
			// Note: The dater returns a Swing JComponent, which won't work in REST
			// For now, just show event count
			// If there's a dater configured, use it to render events
			if(contract.getDater()!=null){
				RestRenderingManager manager=(RestRenderingManager)ProgramStarter.getRenderingManager();
				RestRenderingManager.RestDateRenderingContext daterCtx=new RestRenderingManager.RestDateRenderingContext(cell,manager);
				contract.getDater().render(events,date,daterCtx);
			}
		}

		return cell;
	}

	private String getMonthYearLabel(YearMonth month){
		String[] monthNames={"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
		return monthNames[month.getMonthValue()-1]+" "+month.getYear();
	}

	public void renderPreview(FeatureRenderingContext ctx){
		// Preview rendering not needed for REST
	}
}
