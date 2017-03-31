package org.openlmis.report.view;

import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.report.model.report.PipelineConsumptionLineItem;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("pipelineXml")
public class PipelineXmlView extends AbstractView {

  Document doc;

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType("application/xml");
    response.setHeader("Content-Transfer-Encoding", "binary");
    response.setHeader("Content-Disposition","attachment; filename=\"pipeline-export.xml\"");
    response.setCharacterEncoding("UTF-8");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    doc = dBuilder.newDocument();
    Element rootElement = doc.createElement("Export_File");
    doc.appendChild(rootElement);
    // Write headers
    writeHeader(rootElement, (ProcessingPeriod) model.get("period"));
    writeProducts(rootElement, (List<Product>) model.get("products"));
    writeRecords(rootElement, (List<PipelineConsumptionLineItem>) model.get("consumptions"), (ProcessingPeriod) model.get("period"));
    writeResponse(response, doc);
  }

  private void writeRecords(Element rootElement, List<PipelineConsumptionLineItem> consumptions, ProcessingPeriod period) {
    Element records = createElement(rootElement, "Records", "");
    for (PipelineConsumptionLineItem lineItem : consumptions) {
      Element record = createElement(records, "Record", "");
      createElement(record, "strProductID", lineItem.getProductCode());
      createElement(record, "dtmPeriod", period.getEndDate().toString());
      createElement(record, "lngConsumption", lineItem.getConsumption().toString());
      createElement(record, "lngAdjustments", lineItem.getAdjustment().toString());
    }
  }

  private void writeProducts(Element rootElement, List<Product> products) {
    Element productElements = createElement(rootElement, "Products", "");
    for (Product product : products) {
      Element pro = createElement(productElements, "Product","");
      createElement(pro, "strName", product.getFullName());
      createElement(pro, "strProductID", product.getCode());
      createElement(pro, "Source", "ELMIS");
      createElement(pro, "UserDefined", "false");
      createElement(pro, "ProductGroup", "");
      createElement(pro, "InnovatorName", "");
      Element UOM = createElement(pro, "BaseUOM", "");
      createElement(UOM, "LowestUnitQty", product.getPackSize().toString());
      createElement(UOM, "LowestUnitMeasure", product.getDispensingUnit());
      createElement(UOM, "QuantificationFactor", "1"); //TODO - what is this


    }
  }

  private Element createElement(Element parent, String name, String value) {
    Element childElement = doc.createElement(name);
    childElement.setTextContent(value);
    parent.appendChild(childElement);
    return childElement;
  }

  private void writeHeader(Element rootElement, ProcessingPeriod period) {
    Element fileHeader = doc.createElement("File_Header");
    createElement(fileHeader, "System_Name", "");
    createElement(fileHeader, "FileType", "Forecast");
    createElement(fileHeader, "dtmDataExported", new Date().toString());
    createElement(fileHeader, "dtmStart", period.getStartDate().toString());
    createElement(fileHeader, "dtmEnd", period.getEndDate().toString());
    createElement(fileHeader, "dblDataInterval", "1");
    createElement(fileHeader, "SourceName", "ELMIS");
    rootElement.appendChild(fileHeader);
  }


  private void writeResponse(HttpServletResponse response, Document doc) throws IOException, TransformerException {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer();

    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(response.getWriter());
    try {
      transformer.transform(source, result);
    } catch (javax.xml.transform.TransformerException exp) {
      logger.warn("Transformer Warning: ", exp);
    }
  }
}
