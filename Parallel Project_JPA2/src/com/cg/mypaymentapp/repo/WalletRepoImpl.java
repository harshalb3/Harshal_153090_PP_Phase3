package com.cg.mypaymentapp.repo;

import java.awt.Window.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.cg.mypaymentapp.DBUtil.DBUtil;
import com.cg.mypaymentapp.DBUtil.JPAUtil;
import com.cg.mypaymentapp.beans.Customer;
import com.cg.mypaymentapp.beans.Transactions;
import com.cg.mypaymentapp.beans.Wallet;
import com.cg.mypaymentapp.exception.InvalidInputException;


public class WalletRepoImpl implements WalletRepo 
{
	private Map<String, Customer> data; 
	private Map<String,ArrayList<String>> trans;
	private EntityManager entityManager;
	
	
	public WalletRepoImpl()
	{
		data = new HashMap<String, Customer>();
		entityManager = JPAUtil.getEntityManager();
	}

	public WalletRepoImpl(Map<String, Customer> data) 
	{
		super();
		this.data = data;
	}
	
	@Override
	public boolean save(Customer customer) 
	{	
			entityManager.persist(customer);
		
		return true;
	}
	

	@Override
	public Customer findOne(String mobileNo) 
	{
		
		return entityManager.find(Customer.class, mobileNo);
	
}
	
	@Override
	public Customer depositAmount(String mobileNo, BigDecimal amount) {
		
		Customer cust=null;		
		
//		String str="select wall from Wallet wall where c_mobile_no=:cmno";
//		TypedQuery<Customer> query=entityManager.createQuery(str,Customer.class);
//		query.setParameter("cmno", mobileNo);
//		
		
	
		
		
		cust=new Customer();
		
		cust=entityManager.find(Customer.class, mobileNo);
		
//		cust=query.getSingleResult();
		
		BigDecimal bal1;
		
		bal1=cust.getWallet().getBalance();
		
		bal1=bal1.add(amount);
		
		Wallet wallet=new Wallet(bal1);
		
		cust.setWallet(wallet);
		
		entityManager.merge(cust);
		
		Transactions t=new Transactions();
		
		t.setMobileNo(mobileNo);
		t.setTransaction(amount +" was deposited");
		
		entityManager.persist(t);
		
		return cust;
	}
		
	
	@Override
	public Customer withdrawAmount(String mobileNo, BigDecimal amount) {
		
		Customer cust=null;

//	String str="select wall from Wallet wall where c_mobile_no=:cmno";
//	TypedQuery<Customer> query=entityManager.createQuery(str,Customer.class);
//	query.setParameter("cmno", mobileNo);

	cust=new Customer();

	cust=entityManager.find(Customer.class, mobileNo);
	
//	cust=query.getSingleResult();

	BigDecimal bal1;

	bal1=cust.getWallet().getBalance();

	bal1=bal1.subtract(amount);

	Wallet wallet=new Wallet(bal1);

	cust.setWallet(wallet);
	
	entityManager.merge(cust);
	
	Transactions t=new Transactions();
	
	t.setMobileNo(mobileNo);
	t.setTransaction(amount +" was withdrawn");
	
	entityManager.persist(t);
	
	return cust;

		
	}

	
	@Override
	public Customer fundTransfer(String sourceMobileNo, String targetMobileNo, BigDecimal amount) {
		
		Customer cust=null;
		
				cust=withdrawAmount(sourceMobileNo,amount);
				depositAmount(targetMobileNo,amount);			
			
				
				Transactions t=new Transactions();
				t.setMobileNo(sourceMobileNo);
				t.setTransaction(amount +" was send to mobile no "+targetMobileNo);
				entityManager.persist(t);
				
				Transactions t1=new Transactions();
				t1.setMobileNo(targetMobileNo);
				t1.setTransaction(amount +" was added from mobile no "+sourceMobileNo);
				entityManager.persist(t1);
				
		return cust;
	}


	@Override
	public List<String> showTransaction(String mobileNo) {
		
		
		List<String> message=new ArrayList<String>();
		
		Transactions trans=new Transactions();
		
		List<Transactions> message1=new ArrayList<Transactions>();
		
		
		String str="select trans from Transactions trans where trans.mobileNo=:cmno";
		TypedQuery<Transactions> query=entityManager.createQuery(str, Transactions.class);
		query.setParameter("cmno",mobileNo);
		
		message1=query.getResultList();
		

		Iterator<Transactions> it=message1.iterator();
		
		
		while(it.hasNext())
		{
			message.add(it.next().getTransaction());
		}
		
		return message;

	}
	
	
	@Override
	public void startTransaction() {
		// TODO Auto-generated method stub
		entityManager.getTransaction().begin();
	}

	@Override
	public void commitTransaction() {
		// TODO Auto-generated method stub
		entityManager.getTransaction().commit();
	}
	
	
	public Map<String, ArrayList<String>> getTrans() {
		return trans;
	}

	public void setTrans(Map<String, ArrayList<String>> trans) {
		this.trans = trans;
	}

	

	
	
	
	
	

	
	
}
