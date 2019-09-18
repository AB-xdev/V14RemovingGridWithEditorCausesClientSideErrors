package vbt.poc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.component.grid.editor.EditorOpenEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;


/**
 * Copyright (c) 2019 XDev Software
 * 
 * @author abierler
 *
 * @param <T>
 */
public abstract class GridEditorComponent<T> extends Composite<VerticalLayout>
{
	private final Span spnHeader = new Span();
	
	private final Grid<T> grid = new Grid<>();
	
	protected final Button btnEditSave = new Button("Confirm");
	private final Button btnEditCancel = new Button("Cancel");
	
	private final List<Button> editButtons = new ArrayList<>();
	
	private final Binder<T> binder = new Binder<>();
	
	private final Button btnNew = new Button("Create new / Add");
	
	private Optional<T> createdItem = Optional.empty();
	
	private final Class<T> clazz;
	
	public GridEditorComponent(final Class<T> clazz)
	{
		Objects.requireNonNull(clazz);
		this.clazz = clazz;
	}
	
	public void initBasicUI()
	{
		this.getBtnNew().addClickListener(this::onBtnNewClick);
		
		final VerticalLayout rootLayout = getContent();
		rootLayout.setWidthFull();
		rootLayout.add(this.spnHeader, this.btnNew, this.grid);
	}
	
	public Span getSpnHeader()
	{
		return this.spnHeader;
	}
	
	public Button getBtnNew()
	{
		return this.btnNew;
	}
	
	public void initEditor()
	{
		final Editor<T> editor = this.getEditor();
		editor.setBuffered(true);
		editor.setBinder(this.binder);
		
		editor.addOpenListener(this::onEditorOpen);
		editor.addCloseListener(this::onEditorClose);
		
		this.btnEditSave.addClickListener(this::onBtnEditorSaveInternal);
		this.btnEditCancel.addClickListener(this::onBtnEditorCancel);
	}
	
	private void onEditorOpen(final EditorOpenEvent<T> e)
	{
		this.editButtons.stream().forEach(button -> button.setEnabled(!e.getSource().isOpen()));
	}
	
	private void onEditorClose(final EditorCloseEvent<T> e)
	{
		this.editButtons.stream().forEach(button -> button.setEnabled(!e.getSource().isOpen()));
	}
	
	private void onBtnEditorSaveInternal(final ClickEvent<Button> e)
	{
		if(this.onBtnEditorSaveIsValid(e))
		{
			final T savedItem = this.getEditor().getItem();
			
			this.getEditor().save();
			
			this.createdItem = Optional.empty();
			
			this.afterSavingItem(savedItem);
		}
		
		
	}
	
	
	public void afterSavingItem(final T savedItem)
	{
		// Optional
	}
	
	
	private HorizontalLayout getToolbarColumnContent(final T item)
	{
		final HorizontalLayout layout = new HorizontalLayout();
		final Button btnEdit = new Button("Edit");
		final Button btnDelete = new Button("Delete");
		this.editButtons.add(btnEdit);
		
		layout.add(btnEdit, btnDelete);
		
		btnEdit.addClickListener(e ->
		{
			this.onToolbarBtnEdit(item);
		});
		
		btnDelete.addClickListener(e ->
		{
			this.onToolbarBtnDelete(item);
		});
		
		return layout;
	}
	
	protected boolean onBtnEditorSaveIsValid(final ClickEvent<Button> e)
	{
		final BinderValidationStatus<T> valStatus = this.getBinder().validate();
		return valStatus.isOk();
		
	}
	
	private void onBtnEditorCancel(final ClickEvent<Button> e)
	{
		this.getEditor().cancel();
		
		this.removeCreatedItem();
	}
	
	private void removeCreatedItem()
	{
		if(this.createdItem.isPresent())
		{
			this.removeItem(this.createdItem.get());
		}
		this.createdItem = Optional.empty();
	}
	
	public void addGridManagementButtons()
	{
		// @formatter:off
		this.grid
		.addColumn(
			new ComponentRenderer<>(
				item -> this.getToolbarColumnContent(item)))
		.setAutoWidth(true)
		.setEditorComponent(
			new HorizontalLayout(
				this.btnEditSave,
				this.btnEditCancel));
		// @formatter:on
	}
	
	protected void onBtnNewClick(final ClickEvent<Button> ev)
	{
		this.addNewItem();
	}
	
	private void addNewItem()
	{
		try
		{
			if(this.getEditor().isOpen())
			{
				this.getEditor().cancel();
			}
			
			this.removeCreatedItem();
			
			final T newInstance = this.clazz.newInstance();
			
			final List<T> currItems = this.getAllItems();
			currItems.add(0, newInstance);
			this.setItems(currItems);
			
			this.grid.select(newInstance);
			
			this.createdItem = Optional.of(newInstance);
			
			this.getEditor().editItem(newInstance);
			
		}
		catch(IllegalAccessException | InstantiationException ex)
		{
			throw new RuntimeException(
				String.format(
					"Failed to create Instance of %s",
					this.clazz.getName()),
				ex);
		}
	}
	
	private List<T> getAllItems()
	{
		return this.grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}
	
	protected void onToolbarBtnEdit(final T item)
	{
		this.getEditor().editItem(item);
	}
	
	private void onToolbarBtnDelete(final T item)
	{
		this.removeItem(item);
		this.afterRemovingItem(item);
	}
	
	public Editor<T> getEditor()
	{
		return this.grid.getEditor();
	}
	
	
	public boolean removeItem(final T item)
	{
		final List<T> allItems = this.getAllItems();
		
		final boolean success = allItems.remove(item);
		
		this.setItems(allItems);
		
		return success;
	}
	
	public void afterRemovingItem(final T removedItem)
	{
		// Optional
	}
	
	public void setItems(final List<T> items)
	{
		this.getEditor().cancel();
		this.grid.setItems(items);
	}
	
	public List<T> getItems()
	{
		final List<T> items = this.getAllItems();
		
		if(this.createdItem.isPresent())
		{
			items.remove(this.createdItem.get());
		}
		
		return items;
	}
	
	public void refreshGrid()
	{
		this.grid.getDataProvider().refreshAll();
	}
	
	public Grid<T> getGrid()
	{
		return this.grid;
	}
	
	public Binder<T> getBinder()
	{
		return this.binder;
	}
	
	public void setHeaderTextTranslationKey(final String key)
	{
		this.setHeaderText(this.getTranslation(key));
	}
	
	public void setHeaderText(final String text)
	{
		this.spnHeader.setText(text);
	}
	
}
