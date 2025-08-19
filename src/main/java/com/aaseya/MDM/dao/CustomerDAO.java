package com.aaseya.MDM.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.MDM.dto.CustomerDetailsDTO;
import com.aaseya.MDM.dto.CustomerStatusCountDTO;
import com.aaseya.MDM.model.Customer;
import com.aaseya.MDM.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class CustomerDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@PersistenceContext
    private EntityManager entityManager;
	private final CriteriaPaginationUtil paginationUtil;

	@Autowired
	public CustomerDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}


	public void saveCustomer(Customer customer) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			if (session.contains(customer)) {
				// The entity is already managed
				session.merge(customer);
			} else {
				// The entity is detached
				customer = (Customer) session.merge(customer);
				session.merge(customer);
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	public Customer findById(Long customerId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> root = criteriaQuery.from(Customer.class);
        
        // Create the query to find the customer by ID
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("customerId"), customerId));
        
        // Execute the query and get the result
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
	
	
	
	public Page<CustomerDetailsDTO> getAllCustomerDetails(Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
		Root<Customer> root = query.from(Customer.class);

		query.select(root).orderBy(cb.asc(root.get("customerId")));

		Page<Customer> pagedCustomers = paginationUtil.getPaginatedData(query, root, pageable, Customer.class);

		return pagedCustomers.map(c -> new CustomerDetailsDTO(
			    "C" + c.getCustomerId(), 
			    c.getCustomer_name(),
			    c.getCustomer_type(),
			    c.getRisk(),
			    null,  // reviewStatus is not applicable here
			    c.getStatus()
			));
	}
	
	
	public CustomerStatusCountDTO getCustomerStatusCounts() {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Query to get the total count of cases
	    CriteriaQuery<Long> totalQuery = cb.createQuery(Long.class);
	    Root<Customer> totalRoot = totalQuery.from(Customer.class);
	    totalQuery.select(cb.count(totalRoot));
	    long totalCount = entityManager.createQuery(totalQuery).getSingleResult();

	    // Get counts for each status
	    long in_progress = getStatusCount(cb, "in_progress");
	    long pending = getStatusCount(cb, "pending");
	    long completed = getStatusCount(cb, "completed");
	    long rejected = getStatusCount(cb, "rejected");

	    return new CustomerStatusCountDTO(in_progress, pending, completed, rejected, totalCount);
	}

	private long getStatusCount(CriteriaBuilder cb, String status) {
	    CriteriaQuery<Long> query = cb.createQuery(Long.class);
	    Root<Customer> root = query.from(Customer.class);

	    Predicate statusPredicate = cb.equal(cb.lower(root.get("status")), status);
	    query.select(cb.count(root)).where(statusPredicate);

	    return entityManager.createQuery(query).getSingleResult();
	}
	
	public Page<CustomerDetailsDTO> getCustomerForReview(Pageable pageable) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
	    Root<Customer> root = query.from(Customer.class);
    
	    query.select(root)
	         .where(cb.isNotNull(root.get("reviewStatus")))
	         .orderBy(cb.asc(root.get("customerId")));

	    Page<Customer> pagedCustomers = paginationUtil.getPaginatedData(query, root, pageable, Customer.class);

	    return pagedCustomers.map(c -> new CustomerDetailsDTO(
	    	    "C" + c.getCustomerId(), 
	    	    c.getCustomer_name(),
	    	    c.getCustomer_type(),
	    	    c.getRisk(),
	    	    c.getReviewStatus(),
	    	    null  // status is not applicable here
	    	));
	}
  
	//getCasesbyReviewstatus
		public Map<String, Long> getCasesCountByReviewStatus() {
		    Session session = null;
		    Transaction transaction = null;
		    Map<String, Long> statusCounts = new HashMap<>();

		    try {
		        session = sessionFactory.openSession();
		        transaction = session.beginTransaction();

		        CriteriaBuilder cb = session.getCriteriaBuilder();
		        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		        Root<Customer> root = cq.from(Customer.class);

		        // Selecting reviewStatus and count
		        cq.multiselect(root.get("reviewStatus"), cb.count(root))
		          .groupBy(root.get("reviewStatus"));

		        Query query = session.createQuery(cq);
		        List<Tuple> results = query.getResultList();

		        long total = 0;
		        for (Tuple tuple : results) {
		            String status = tuple.get(0, String.class);
		            if (status == null) {
		                status = "UNKNOWN"; // Replace null key with a valid string
		            }
		            Long count = tuple.get(1, Long.class);
		            statusCounts.put(status, count);
		            total += count;
		        }

		        statusCounts.put("total", total);
		        transaction.commit();
		    } catch (Exception e) {
		        if (transaction != null) {
		            transaction.rollback();
		        }
		        e.printStackTrace();
		    } finally {
		        if (session != null) {
		            session.close();
		        }
		    }

		    return statusCounts;
		}
		
		public Customer findCustomerById(Long customerId) {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
			Root<Customer> root = query.from(Customer.class);

			Predicate predicate = cb.equal(root.get("customerId"), customerId);
			query.select(root).where(predicate);

			return entityManager.createQuery(query).getSingleResult();
		}
		
		@Transactional
		public void saveCustomerCreditScore(Customer customer) {
			entityManager.merge(customer);
		}
	}



