package vbt.poc;

import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


public class DemoComponentTab extends GridEditorComponent<DemoObject>
{
	private final TextField txtName = new TextField();
	
	public DemoComponentTab()
	{
		super(DemoObject.class);
		
		initBasicUI();
		initUI();
		initEditor();
		configureEditor();
		initColumns();
		addGridManagementButtons();
		initBinder();
	}
	
	public void initUI()
	{
		setHeaderText("Demo Comp With Editor :)");
	}
	
	public void configureEditor()
	{
		final Editor<DemoObject> editor = getGrid().getEditor();
		
		editor.addOpenListener(e -> txtName.focus());
	}
	
	public void initColumns()
	{
		// @formatter:off
		getGrid()
		.addColumn(DemoObject::getName)
		.setHeader("Name")
		.setAutoWidth(true)
		.setResizable(true)
		.setSortable(true)
		.setEditorComponent(txtName);
		// @formatter:on
	}
	
	public void initBinder()
	{
		final Binder<DemoObject> binder = getBinder();
		
		// @formatter:off
		binder
		.forField(txtName)
		.bind(DemoObject::getName,DemoObject::setName);
		
		// @formatter:on
	}
	
}
