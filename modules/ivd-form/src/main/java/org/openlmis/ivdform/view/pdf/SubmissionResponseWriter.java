/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.ivdform.view.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.OutputStream;
import java.util.Map;

public class SubmissionResponseWriter extends PdfWriter {
  private static final Font ERROR_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, Color.RED);
  private static final Font SUCCESS_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, Color.BLACK);
  private static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);


  public SubmissionResponseWriter(PdfDocument document, OutputStream stream) throws DocumentException {
    super(document, stream);
    document.addWriter(this);
    document.setPageSize(PAGE_SIZE);
    document.setMargins(20, 20, 20, 20);
    this.setViewerPreferences(getViewerPreferences());
  }

  public void buildWith(Map<String, Object> model) throws DocumentException {
    SubmissionResponseModel responseObject = (SubmissionResponseModel) model.get("STATUS");
    document.open();

    if(responseObject.getIsError()) {
      Paragraph message = new Paragraph(responseObject.getMessage(), ERROR_FONT);
      message.setAlignment("Center");
      document.add(message);
    }else{
      Paragraph message = new Paragraph(responseObject.getMessage(), SUCCESS_FONT);
      message.setAlignment("Center");
      document.add(message);
    }
    document.close();
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }


}
