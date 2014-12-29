package org.bibliarij.basphonebook.ui;

import org.bibliarij.basphonebook.db.DAO;
import org.bibliarij.basphonebook.db.Entry;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.List;

/**
 * Entries listbox composer
 * Created by Bibliarij on 02.08.2014.
 */
public class ListboxComposer extends SelectorComposer<Component> {
    @Wire
    private Listbox entriesListbox;

    /**
     * Actions carried out after page composing
     */
    public void doAfterCompose(Component component) throws Exception {
        super.doAfterCompose(component);

        entriesListbox.setItemRenderer(new EntryItemRenderer());

        refreshModel(DAO.getAllEntryEntities());
    }

    /**
     * Load data to listbox
     */
    public void refreshModel(List<Entry> entryList){
        entriesListbox.setModel(new ListModelList<Entry>(entryList));
    }
}
