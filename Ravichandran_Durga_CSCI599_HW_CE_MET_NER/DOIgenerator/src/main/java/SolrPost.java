import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ExpandParams;
import org.apache.solr.common.util.NamedList;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
public class SolrPost 
{
	public static void main(String[] args) 
	{
	  try 
	  {
		  //copyFolder(new File("/home/durga/solr-5.5.0/server/solr/polarcollection"), new File("./polarcollection"));
		  String fileName = "/home/durga/Desktop/USCstuff/BigData/Assignments/nerdata1/1DB25326EF677104EAD787C4E4005FD18242D46C38B53C2B2BDA38ADCA208A08.pdf"; 
		  String solrId = "polar.usc.edu/qwerty234";
		  indexFilesSolrCell(fileName, solrId);
		  
	  } 
	  catch (Exception ex) 
	  {
		  System.out.println(ex.toString());
	  }
  }
  public static void copyFolder(File source, File destination)
  {
	      if (source.isDirectory())
	      {
	          if (!destination.exists())
	          {
	              destination.mkdirs();
	          }

	          String files[] = source.list();

	          for (String file : files)
	          {
	              File srcFile = new File(source, file);
	              File destFile = new File(destination, file);

	              copyFolder(srcFile, destFile);
	          }
	      }
	      else
	      {
	          InputStream in = null;
	          OutputStream out = null;

	          try
	          {
	              in = new FileInputStream(source);
	              out = new FileOutputStream(destination);

	              byte[] buffer = new byte[1024];

	              int length;
	              while ((length = in.read(buffer)) > 0)
	              {
	                  out.write(buffer, 0, length);
	              }
	          }
	          catch (Exception e)
	          {
	              try
	              {
	                  in.close();
	              }
	              catch (IOException e1)
	              {
	                  e1.printStackTrace();
	              }

	              try
	              {
	                  out.close();
	              }
	              catch (IOException e1)
	              {
	                  e1.printStackTrace();
	              }
	          }
	      }
  }
  public static void createCore(String solrpath) throws SolrServerException, IOException
  {
	  
	  SystemDefaultHttpClient httpClient1 = new SystemDefaultHttpClient();
	  HttpSolrClient client = new HttpSolrClient("http://localhost:8983/solr", httpClient1);
	  CoreAdminResponse e = new CoreAdminRequest().createCore("collection2","collection2", client);
  }
  public static void indexFilesSolrCell(String fileName, String solrId) throws IOException, SolrServerException 
  {
	  String urlString = "http://localhost:8983/solr/polarcollection2"; 
	  SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
	  HttpSolrClient solr = new HttpSolrClient(urlString, httpClient);
	  ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
  	  up.addFile(new File(fileName),"");
  	  up.setParam("literal.id", solrId);
  	  up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
  	  solr.request(up);
	  //QueryResponse rsp = solr.query(new SolrQuery("*:*"));
	  //System.out.println(rsp);
  }
}