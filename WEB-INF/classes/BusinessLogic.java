/* Name: Hunter Busa
  Course: CNT 4714 – Fall 2019 – Project Four
  Assignment title: A Three-Tier Distributed Web-Based Application
  Date: December 1, 2019
*/

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;



public class BusinessLogic extends HttpServlet
{

	private Connection connection;
	private Statement statement;
	
	public void init( ServletConfig config ) throws ServletException
	   {
	      // attempt database connection and create Statement like in the SurveyServlet example
	      try 
	      {
	         Class.forName( config.getInitParameter( "databaseDriver" ) );
	           connection = DriverManager.getConnection( 
	           config.getInitParameter( "databaseName" ),
	           config.getInitParameter( "username" ),
	           config.getInitParameter( "password" ) );
	       

	          Class.forName("com.mysql.jdbc.Driver");
				 connection = DriverManager.getConnection("jdbc:mysql://localhost:3312/project4", "root", "root" );
	         // create Statement to query database
	         statement = connection.createStatement();
	      } // end try
	      // for any exception throw an UnavailableException to 
	      // indicate that the servlet is not currently available
	      catch ( Exception exception ) 
	      {
	         exception.printStackTrace();
	         throw new UnavailableException( exception.getMessage() );
	      } // end catch
	   }  // end method init 
	
 	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
			{
				RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
				dispatcher.forward(request, response);
			}
	
	
	//Post process to get data from jsp page and inspired by last example in 5-2
	protected void doPost(HttpServletRequest request, HttpServletResponse response )
		throws IOException, ServletException
		{
		String commandBox = request.getParameter("commandBox");
		String output = null;
		
		
		
		
		if( commandBox.contains("SELECT") || commandBox.contains("select"))
		{
			try 
			{
				
				output = doSelectQuery(commandBox);
			}
			catch(SQLException e)
			{
				output = "<span>" + "Error executing the SQL statement:" + "<br>" + e.getMessage() + "</span>";
				
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				output = doUpdateQuery(commandBox);
			}
			catch(SQLException e)
			{
				output = "<span>" + "Error executing the SQL statement:" + "<br>" + e.getMessage() + "</span>";
				
				e.printStackTrace();
			}
		}
		
		//Heavily inspired from details pdf
		HttpSession session = request.getSession();
		session.setAttribute("output", output);
		session.setAttribute("commandBox", commandBox);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
		}
	
	
	public String doSelectQuery(String commandBox) throws SQLException
	{
		String output;
		int numColumns;
		String ColumnName = null;
		String RowName = null;
		
		
		ResultSet resultSet = statement.executeQuery(commandBox);
			
		ResultSetMetaData metaData = resultSet.getMetaData();
		numColumns = metaData.getColumnCount();
		for(int i = 1; i <= numColumns; i++)
		{
			ColumnName += "<th scope='col'>" + metaData.getColumnName(i) + "</th>";
		}
			
		String tableOPEN = "<table>";
		String tableBodyHTML = "<tbody>";
		
		while (resultSet.next())
		{
			tableBodyHTML += "<tr>";
			for(int i=1; i <= numColumns; i++)
			{
				tableBodyHTML += "<td scope'row'>" + resultSet.getString(i) + "</th>";
			}
			tableBodyHTML += "</tr>";
		}
		
		tableBodyHTML += "</tbody>";
		
		String tableClosingHTML = "</table></div></div></div>";
		output = tableOPEN + ColumnName + tableBodyHTML + tableClosingHTML;
		
		return output;
	}
	
	public String doUpdateQuery(String commandBox) throws SQLException
	{
		String output = null;
		int UpdatedRows = 0;
		int Quantityabove100Before, Quantityabove100After, Incrementcount;
		ResultSet Beforecheck, Aftercheck;
		
		Beforecheck = statement.executeQuery("select count(*) from shipments where quantity >= 100");
		Beforecheck.next();
		Quantityabove100Before = Beforecheck.getInt(1);
		
		statement.executeUpdate("create table shipmentsBefore like shipments");
		statement.executeUpdate("insert into shipmentsBefore select *from shipments");
		
		UpdatedRows = statement.executeUpdate(commandBox);
		output = " <div> The statement executed succesfully" + "<br>" + UpdatedRows + " row(s) affected </div>";
		
		
		
		Aftercheck = statement.executeQuery("select count(*) from shipments where quantity >= 100");
		Aftercheck.next();
		Quantityabove100After = Aftercheck.getInt(1);
		
		if(Quantityabove100Before < Quantityabove100After)
		{
			Incrementcount = statement.executeUpdate("update suppliers set status = status + 5 where snum is in (select distinct snum from shipments left join shipmentsBefore using (snum, pnum, jnum, quantity) where shipmentsBefore.snum is null)");
			
			output += "<div> Business Logic Detected! - Updating Supplier Status <br>";
			output += "Business Logic updated" + Incrementcount + "supplier status marks. </div>";
		}	
		
		statement.executeUpdate("drop table shipmentsBefore");
		
		return output;
	}
	
	

}
