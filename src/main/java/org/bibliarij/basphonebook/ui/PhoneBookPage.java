package org.bibliarij.basphonebook.ui;

import org.bibliarij.basphonebook.db.DAO;
import org.bibliarij.basphonebook.db.Entry;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.HashMap;
import java.util.List;

/**
 * Main page UI class
 * Created by Bibliarij on 31.07.2014.
 */
public class PhoneBookPage extends SelectorComposer<Component>{
    @Wire
    private Window phonePageWindow;

    @Wire
    private Textbox findFieldTextbox;

    /**
     * "Create" button event handler
     */
    @Listen("onClick=#createEntryButton")
    public void createEntryButtonOnClick(){
        Window window = (Window) Executions.createComponents("entry.zul", phonePageWindow, new HashMap());
        window.doModal();
    }

    /**
     * "Find" button event handler
     */
    @Listen("onClick=#findEntryButton")
    public void findEntryButtonOnClick(){
        String stringToFind = findFieldTextbox.getValue();
        List<Entry> entries;
        if (stringToFind.isEmpty()){
            entries = DAO.getAllEntryEntities();
        } else {
            entries = DAO.findEntities(stringToFind);
        }

        Listbox listbox = (Listbox) phonePageWindow.getFellow("entriesListbox", true);
        ListboxComposer composer = (ListboxComposer) listbox.getAttribute("$composer");
        composer.refreshModel(entries);
    }
}
