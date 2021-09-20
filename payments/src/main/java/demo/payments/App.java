package demo.payments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class App 
{
	//Create payment from string
	private static Payment CreatePayment(String line) throws UnknownCurrencyException, NumberFormatException {
		String[] datas = line.split(",");
		String userId = datas[0];
		long unixTimestamp = Long.parseLong(datas[1]);
		String country = datas[2];
		String currency = datas[3];
		int amountInCents = Integer.parseInt(datas[4]);
		
		//Set original amount
		MonetaryAmount amount = Monetary.getDefaultAmountFactory().setCurrency(currency).setNumber(amountInCents).create();
		//Set converter
		CurrencyConversion conversionEUR = MonetaryConversions.getConversion("EUR");
		//Get converted currency version
		double convertedAmountsInEUR = amount.with(conversionEUR).getNumber().doubleValueExact() / 100;  // cents => euros : divide with 100
				
		Payment payment = new Payment(userId, unixTimestamp, country, currency, amountInCents, convertedAmountsInEUR);
		
		return payment;
	}
	
    public static void main( String[] args )
    {
    	try (	Stream<String> lines = Files.lines(Paths.get("payments.csv"), Charset.defaultCharset()).skip(1);
    			SessionFactory factory = new Configuration()
    					.configure("hibernate.cfg.xml")
    					.addAnnotatedClass(Payment.class)
    					.buildSessionFactory();
    			Session session = factory.getCurrentSession();
    			BufferedWriter paymentsPerCurrency = new BufferedWriter(new FileWriter("paymentsPerCurrency.csv"));
    			BufferedWriter paymentsPerUser = new BufferedWriter(new FileWriter("paymentsPerUser.csv"));
    			BufferedWriter paymentsPerDay = new BufferedWriter(new FileWriter("paymentsPerDay.csv"));) {
    		    		
    		session.beginTransaction();
    		
    		//Read lines from payments.csv, save payments into DB
    		lines.forEachOrdered(line -> session.save(CreatePayment(line)));
    
    		//Get payments per currencies
    	    String hql = "SELECT P.currency, SUM(P.amountInCents) FROM Payment P GROUP BY P.currency";
    	    Query<Object[]> query = session.createQuery(hql);
    	    List<Object[]> results = query.list();
    	    paymentsPerCurrency.write("Currency,AmountInCents\n");
    	    for (Object[] objects : results) {
    	    	paymentsPerCurrency.write(objects[0] + "," + objects[1] + "\n");
    	    }
    	    
    	    //Get payments per users
    	    hql = "SELECT P.userID, P.currency, SUM(P.amountInCents) FROM Payment P GROUP BY P.userID";
    	    query = session.createQuery(hql);
    	    results = query.list();
    	    paymentsPerUser.write("UserID,Currency,AmountInCents\n");
    	    for (Object[] objects : results) {
    	    	paymentsPerUser.write(objects[0] + "," + objects[1] + ',' + objects[2] + "\n");
    	    }
    	    
    	    //Get payments per days in euros
    	    hql = "SELECT floor(P.unixTimestamp / 86400), sum(P.amountInEuro) FROM Payment P " 
    	    		+ "GROUP BY floor(P.unixTimestamp / 86400)";  // 86400 == 60 * 60 * 24 (secs * mins * days)
    	    query = session.createQuery(hql);
    	    results = query.list();
    	    paymentsPerDay.write("unixTimestamp,AmountInEuro\n");
    	    for (Object[] objects : results) {
    	    	Long day = Long.valueOf(((Integer)objects[0])) * 60 * 60 * 24;
    	    	paymentsPerDay.write(day + "," + objects[1] + "\n");
    	    }
    	    
			session.getTransaction().commit();
    	} catch (UnknownCurrencyException e) {
			System.out.println("A fájlban megadott \"" + e.getCurrencyCode() + "\" nem megfelelő valutaformátum!");
    	} catch (NumberFormatException e) {
			System.out.println("Hibás a fájl tartalma!");
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
