package org.bibliarij.basphonebook.ui;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.bibliarij.basphonebook.db.DAO;
import org.bibliarij.basphonebook.db.Entry;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for listbox item render
 * Created by Bibliarij on 02.08.2014.
 */
public class EntryItemRenderer implements ListitemRenderer<Entry>{
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Listbox item render
     */
    @Override
    public void render(Listitem item, final Entry data, int index) throws Exception {
        item.setValue(data);

        new Listcell(data.getSurnameNamePatronymic()).setParent(item);
        String date = "";
        if (data.getBirthDate() != null){
            date = simpleDateFormat.format(data.getBirthDate());
        }
        new Listcell(date).setParent(item);
        new Listcell(data.getAddress()).setParent(item);
        new Listcell(data.getPhoneNumber()).setParent(item);

        Button editButton = new Button();
        editButton.setLabel("Редактировать");
        editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

            /**
             * "Edit" button event handler
             */
            @Override
            public void onEvent(Event event) throws Exception {
                Map<String, Entry> map = new HashMap<String, Entry>();
                map.put("entry", data);

                Button targetButton = (Button) event.getTarget();
                Listcell targetButtonParent1 = (Listcell) targetButton.getParent();
                Listitem targetButtonParent2 = (Listitem) targetButtonParent1.getParent();
                Listbox targetButtonParent3 = (Listbox) targetButtonParent2.getParent();
                Window targetButtonParent4 = (Window) targetButtonParent3.getParent();

                Window window = (Window) Executions.createComponents("/entry.zul",
                        targetButtonParent4, map);
                window.doModal();
            }
        });

        Listcell editButtonCell = new Listcell();
        editButtonCell.appendChild(editButton);
        editButtonCell.setParent(item);

        Button exportToExcelButton = new Button();
        exportToExcelButton.setLabel("Экспорт в Excel");
        exportToExcelButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

            /**
             * "Export to Excel" button event handler
             */
            @Override
            public void onEvent(Event event) throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                WritableWorkbook workbook = Workbook.createWorkbook(baos);
                WritableSheet sheet = workbook.createSheet("First Sheet", 0);

                jxl.write.Label surnameNamePatronymicLabel = new jxl.write.Label(0, 0, data.getSurnameNamePatronymic());
                sheet.addCell(surnameNamePatronymicLabel);

                DateTime birthdateCell = new DateTime(1, 0, data.getBirthDate());
                sheet.addCell(birthdateCell);

                jxl.write.Label addressLabel = new jxl.write.Label(2, 0, data.getAddress());
                sheet.addCell(addressLabel);

                jxl.write.Label phoneNumberLabel = new jxl.write.Label(3, 0, data.getPhoneNumber());
                sheet.addCell(phoneNumberLabel);

                Blob photo = data.getPhoto();
                if (photo != null){
                    WritableImage photoCell = new WritableImage(4, 0, 20, 20, photo.getBytes(1, (int) photo.length()));
                    sheet.addImage(photoCell);
                }

                workbook.write();
                workbook.close();

                Filedownload.save(baos.toByteArray(), "application/vnd.ms-excel", "entry.xls");
                baos.close();
            }
        });

        Listcell exportToExcelButtonCell = new Listcell();
        exportToExcelButtonCell.appendChild(exportToExcelButton);
        exportToExcelButtonCell.setParent(item);

        Button exportToPDFButton = new Button();
        exportToPDFButton.setLabel("Экспорт в PDF");
        exportToPDFButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

            /**
             * "Export to PDF" button event handler
             */
            @Override
            public void onEvent(Event event) throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                PdfWriter writer = PdfWriter.getInstance(document, baos);

                document.open();

                PdfPTable table = new PdfPTable(5);
                table.addCell(data.getSurnameNamePatronymic());

                String date = "";
                if (data.getBirthDate() != null){
                    date = simpleDateFormat.format(data.getBirthDate());
                }
                table.addCell(date);

                table.addCell(data.getAddress());
                table.addCell(data.getPhoneNumber());

                Blob photo = data.getPhoto();
                if (photo != null){
                    Image image = Image.getInstance(photo.getBytes(1, (int) photo.length()));
                    image.scaleAbsolute(100, 100);
                    table.addCell(image);
                } else {
                    table.addCell("");
                }

                document.add(table);
                document.close();

                Filedownload.save(baos.toByteArray(), "application/pdf", "entry.pdf");
                baos.close();
            }
        });

        Listcell exportToPDFButtonCell = new Listcell();
        exportToPDFButtonCell.appendChild(exportToPDFButton);
        exportToPDFButtonCell.setParent(item);

        Button deleteButton = new Button();
        deleteButton.setLabel("Удалить");
        deleteButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

            /**
             * "Delete" button event handler
             */
            @Override
            public void onEvent(Event event) throws Exception {
                DAO.deleteEntry(data);
                Messagebox.show("Запись удалена!");

                Button targetButton = (Button) event.getTarget();
                Listcell targetButtonParent1 = (Listcell) targetButton.getParent();
                Listitem targetButtonParent2 = (Listitem) targetButtonParent1.getParent();
                Listbox targetButtonParent3 = (Listbox) targetButtonParent2.getParent();

                ListboxComposer composer = (ListboxComposer) targetButtonParent3.getAttribute("$composer");
                composer.refreshModel(DAO.getAllEntryEntities());
            }
        });

        Listcell deleteButtonCell = new Listcell();
        deleteButtonCell.appendChild(deleteButton);
        deleteButtonCell.setParent(item);
    }
}
