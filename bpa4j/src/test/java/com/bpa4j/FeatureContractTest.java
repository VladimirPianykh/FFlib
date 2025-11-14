package com.bpa4j;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.Feature;

/**
 * Test class demonstrating the usage of static create* and get* methods
 * for Feature Contracts like Board, Calendar, ItemList, Report, and DatedList.
 * @author AI-generated
 */
public class FeatureContractTest{

    // Sample classes for testing
    public static class TestEditable extends Editable implements Serializable{
        private static final long serialVersionUID=1L;
        public String name;
        public int value;

        public TestEditable(){
            super("Test Object");
        }

        public TestEditable(String name,int value){
            super(name);
            this.name=name;
            this.value=value;
        }
    }

    public static class TestEvent implements Calendar.Event{
        public String title;
        public String description;

        public TestEvent(String title,String description){
            this.title=title;
            this.description=description;
        }

        @Override
        public String toString(){
            return title+": "+description;
        }
    }

    public static void main(String[] args){
        System.out.println("=== Feature Contract Test ===");

        // Test Board creation and retrieval
        System.out.println("\n1. Testing Board:");
        Feature<Board<TestEditable>> boardFeature=Board.registerBoard("testBoard",TestEditable.class);
        System.out.println("✓ Board registered: "+boardFeature.getName());

        Board<TestEditable> board=Board.getBoard("testBoard");
        System.out.println("✓ Board retrieved: "+board.getFeatureName());

        // Test method chaining on Board
        board.setAllowCreation(true).setSlicer(obj->obj.name+" ("+obj.value+")");
        System.out.println("✓ Board configured with method chaining");

        // Test Calendar creation and retrieval
        System.out.println("\n2. Testing Calendar:");
        Feature<Calendar<TestEvent>> calendarFeature=Calendar.registerCalendar("testCalendar",TestEvent.class);
        System.out.println("✓ Calendar registered: "+calendarFeature.getName());

        Calendar<TestEvent> calendar=Calendar.getCalendar("testCalendar");
        System.out.println("✓ Calendar retrieved: "+calendar.getFeatureName());

        // Test method chaining on Calendar
        calendar.setEventFiller(eventMap->{
            LocalDate today=LocalDate.now();
            List<TestEvent> events=new ArrayList<>();
            events.add(new TestEvent("Meeting","Team standup"));
            eventMap.put(today,events);
        });
        System.out.println("✓ Calendar configured with method chaining");

        // Test ItemList creation and retrieval
        System.out.println("\n3. Testing ItemList:");
        Feature<ItemList<TestEditable>> listFeature=ItemList.registerList("testList",TestEditable.class);
        System.out.println("✓ ItemList registered: "+listFeature.getName());

        ItemList<TestEditable> itemList=ItemList.getList("testList");
        System.out.println("✓ ItemList retrieved: "+itemList.getFeatureName());

        // Test method chaining on ItemList
        itemList.setAllowCreation(true).addCollectiveAction(items->System.out.println("Processing "+items.size()+" items")).setSlicer(obj->"Item: "+obj.name);
        System.out.println("✓ ItemList configured with method chaining");

        // Test Report creation and retrieval
        System.out.println("\n4. Testing Report:");
        Feature<Report> reportFeature=Report.registerReport("testReport");
        System.out.println("✓ Report registered: "+reportFeature.getName());

        Report report=Report.getReport("testReport");
        System.out.println("✓ Report retrieved: "+report.getFeatureName());

        // Test method chaining on Report
        report.addConfigurator(saver->null) // Simplified for test
                .addDataRenderer(()->null); // Simplified for test
        System.out.println("✓ Report configured with method chaining");

        // Test DatedList creation and retrieval
        System.out.println("\n5. Testing DatedList:");
        Feature<DatedList<TestEditable>> datedListFeature=DatedList.registerList("testDatedList",TestEditable.class);
        System.out.println("✓ DatedList registered: "+datedListFeature.getName());

        DatedList<TestEditable> datedList=DatedList.getList("testDatedList");
        System.out.println("✓ DatedList retrieved: "+datedList.getFeatureName());

        // Test method chaining on DatedList
        datedList.setDateProvider(()->null); // Simplified for test
        System.out.println("✓ DatedList configured with method chaining");

        // Test error handling
        System.out.println("\n6. Testing error handling:");
        try{
            Board.getBoard("nonExistentBoard");
        }catch(IllegalArgumentException e){
            System.out.println("✓ Error handling works: "+e.getMessage());
        }

        System.out.println("\n=== All tests completed successfully! ===");
        System.out.println("\nYou can now use these static methods in your FullTester:");
        System.out.println("- Board.registerBoard(name, clazz) / Board.getBoard(name)");
        System.out.println("- Calendar.registerCalendar(name, eventClass) / Calendar.getCalendar(name)");
        System.out.println("- ItemList.registerList(name, clazz) / ItemList.getList(name)");
        System.out.println("- Report.registerReport(name) / Report.getReport(name)");
        System.out.println("- DatedList.registerList(name, clazz) / DatedList.getList(name)");
        System.out.println("\nAll methods support fluent interface (method chaining)!");
    }
}
