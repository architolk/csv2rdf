package nl.architolk.csv2rdf;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Convert {

  private static final Logger LOG = LoggerFactory.getLogger(Convert.class);

  public static void main(String[] args) {

    if (args.length == 2) {

      LOG.info("Starting conversion");
      LOG.info("Input file: {}",args[0]);
      LOG.info("Ouput file: {}",args[1]);

      try {

        Model model = ModelFactory.createDefaultModel();

        Reader in = new FileReader(args[0]);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
          Map<String,String> map = record.toMap();
          for (Map.Entry<String, String> entry : map.entrySet())
          {
              String value = entry.getValue();
              Resource resource = model.createResource("http://csvrecord/id/row/" + record.getRecordNumber());
              Property property = ResourceFactory.createProperty("http://csvrecord/def#" + entry.getKey());
              if (!value.isEmpty()) {
                resource.addProperty(property,entry.getValue());
              }
          }
        }

        model.write(new FileWriter(args[1]),"TURTLE");
        LOG.info("Done!");
      }
      catch (Exception e) {
        LOG.error(e.getMessage(),e);
      }
    } else {
      LOG.info("Usage: csv2rdf <input.csv> <output.ttl>");
    }
  }
}
