package org.bibliarij.basphonebook.ui;

import org.bibliarij.basphonebook.db.DAO;
import org.bibliarij.basphonebook.db.Entry;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Entry page UI class
 * Created by Bibliarij on 31.07.2014.
 */
public class EntryPage extends SelectorComposer<Component>
{
    @Wire
    private Textbox surnameNamePatronymicTextbox;

    @Wire
    private Datebox birthDateDateBox;

    @Wire
    private Textbox addressTextbox;

    @Wire
    private Textbox phoneNumberTextbox;

    @Wire
    private Image photoImage;

    @Wire
    private Button photoUploadButton;

    @Wire
    private Window entryPageWindow;

    private Entry entry = null;

    /**
     * Actions carried out after page composing
     */
    public void doAfterCompose(Component component){
        try {
            super.doAfterCompose(component);

            Map map = Executions.getCurrent().getArg();
            entry = (Entry) map.get("entry");

            if (entry != null){
                surnameNamePatronymicTextbox.setValue(entry.getSurnameNamePatronymic());
                birthDateDateBox.setValue(entry.getBirthDate());
                addressTextbox.setValue(entry.getAddress());
                phoneNumberTextbox.setValue(entry.getPhoneNumber());
                if (entry.getPhoto() != null){
                    photoImage.setContent(new AImage("photo", entry.getPhoto().getBinaryStream()));
                }
            }
        } catch (Exception e) {
            Messagebox.show("Произошла неизвестная ошибка", null, 0, org.zkoss.zul.Messagebox.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * "Save" button event handler
     */
    @Listen("onClick=#saveButton")
    public void saveButtonOnClick()  {
        Entry newEntry = null;
        if (entry == null){
            newEntry = new Entry();
            newEntry.setId(UUID.randomUUID().toString());
        }else{
            newEntry = entry;
        }

        newEntry.setSurnameNamePatronymic(surnameNamePatronymicTextbox.getValue());
        newEntry.setBirthDate(birthDateDateBox.getValue());
        newEntry.setAddress(addressTextbox.getValue());
        newEntry.setPhoneNumber(phoneNumberTextbox.getValue());
        try {
            if (photoImage.getContent() != null){
                newEntry.setPhoto(new SerialBlob(photoImage.getContent().getByteData()));
            }
        } catch (SQLException e) {
            Messagebox.show("Ошибка базы данных", null, 0, org.zkoss.zul.Messagebox.ERROR);
        }

        if (entry == null){
            DAO.saveEntry(newEntry);
        }else{
            DAO.saveEntry(newEntry);
        }

        Window parent = (Window) entryPageWindow.getParent();
        Listbox listbox = (Listbox) parent.getFellow("entriesListbox", true);
        ListboxComposer composer = (ListboxComposer) listbox.getAttribute("$composer");
        composer.refreshModel(DAO.getAllEntryEntities());

        Messagebox.show("Запись сохранена!");

        entryPageWindow.detach();
    }

    /**
     * "Cancel" button event handler
     */
    @Listen("onClick=#cancelButton")
    public void cancelButtonOnClick()  {
        entryPageWindow.detach();
    }

    /**
     * Photo upload event handler
     */
    @Listen("onUpload=#photoUploadButton")
    public void photoUploadButtonOnUpload(UploadEvent event){
        try {
            photoImage.setContent(new AImage("photo", event.getMedia().getStreamData()));
        } catch (IOException e) {
            Messagebox.show("Ошибка при чтении фотографии", null, 0, org.zkoss.zul.Messagebox.ERROR);
        }
    }
}
