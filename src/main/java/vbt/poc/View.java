package vbt.poc;

import java.util.Arrays;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends Composite<VerticalLayout>
{
	private final Button btnClearTab = new Button("Clear Container");
	private final Button btnSetTab = new Button("Set into Container");
	
	private final DemoComponentTab component = new DemoComponentTab();
	
	private final VerticalLayout container = new VerticalLayout();
	
	public View()
	{
		container.setSizeFull();
		getContent().add(btnClearTab, btnSetTab, container);
		getContent().setSizeFull();
		
		
		btnClearTab.addClickListener(ev -> {
			container.removeAll();
		});
		btnSetTab.addClickListener(ev -> {
			container.addAndExpand(component);
		});
	}
	
	@Override
	protected void onAttach(final AttachEvent attachEvent)
	{
		component.setItems(Arrays.asList(new DemoObject("TEST"), new DemoObject("TEST2")));
	}
}
