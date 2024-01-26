package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	db.listings.aggregate(
		{ $match: {"address.country" : { $regex: "Australia", $options: "i"}} },
		{ $group: { _id: "$address.suburb"} }
	)
	 *
	 */
	public List<String> getSuburbs(String country) {
		System.out.println("Getting suburbs..");
		Criteria criteria = Criteria.where("address.country").regex(country, "i");
		MatchOperation match = Aggregation.match(criteria);
		GroupOperation group = Aggregation.group("address.suburb");
		Aggregation pipeline = Aggregation.newAggregation(match, group);

		AggregationResults<Document> results = template.aggregate(pipeline, "listings", Document.class);
		List<Document> listDoc = results.getMappedResults();
		List<String> returnResult = new LinkedList<>();
		for (Document d : listDoc) {
			String suburb = d.getString("_id");
			if (suburb.isBlank()) {
				continue;
			}
			// System.out.println(suburb);
			returnResult.add(d.getString("_id"));
		}
		return returnResult;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	db.listings.find(
		{
			"address.suburb" : "Bondi",
			price: { $lte: 100 },
			accommodates: { $gte: 2},
			min_nights: { $lte: 3}
		}
	).sort(
		{
			"price": -1
		}
	)
	 *
	 *
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		System.out.println("Finding listings based on search.. ");
		Criteria criteria = Criteria
			.where("address.suburb").regex(suburb, "i")
			.and("price").lte(priceRange)
			.and("accommodates").gte(persons)
			.and("min_nights").lte(duration);
		Query query = Query.query(criteria)
			.with(Sort.by(Sort.Direction.DESC, "price"));
		List<Document> results = template.find(query, Document.class, "listings");
		System.out.println("Results: " + results);

		List<AccommodationSummary> listSummary = new LinkedList<>();
		for (Document d : results) {
			String id = d.getString("_id");
			String name = d.getString("name");
			Integer accommodates = d.getInteger("accommodates");
			float price = d.get("price", Number.class).floatValue();
			System.out.println(id+name+accommodates+price);
			AccommodationSummary summary = new AccommodationSummary();
			summary.setId(id);
			summary.setName(name);
			summary.setAccomodates(accommodates);
			summary.setPrice(price);
			listSummary.add(summary);
		}
		return listSummary;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
