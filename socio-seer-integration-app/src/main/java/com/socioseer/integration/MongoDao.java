package com.socioseer.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.socioseer.common.exception.SocioSeerException;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Repository
public class MongoDao<T> {

  @Autowired
  private MongoTemplate mongoTemplate;


  /**
   * 
   * @param collectionName
   * @param data
   */
  public void save(String collectionName, T data) {
    mongoTemplate.save(data, collectionName);
  }

  /**
   * 
   * @param collectionName
   * @param data
   */
  public void save(String collectionName, List<T> data) {
    mongoTemplate.insert(data, collectionName);
  }

  /**
   * 
   * @param query
   * @param collectionName
   * @return	returns DbObject
   */
  public DBObject get(DBObject query, String collectionName) {
    DBCollection dbCollection = getCollection(collectionName);
    if (dbCollection == null) {
      throw new SocioSeerException(String.format("No collection found with name : %s",
          collectionName));
    }
    return dbCollection.findOne(query);
  }

  /**
   * 
   * @param query
   * @param collectionName
   * @param type
   * @return	returns DBObject list
   */
  public List<DBObject> query(DBObject query, String collectionName, T type) {
    DBCollection collection = getCollection(collectionName);
    DBCursor dbCurser = collection.find(query);
    if (dbCurser != null) {
      List<DBObject> result = new ArrayList<>();
      dbCurser.forEach(dbo -> {
        result.add(dbo);
      });
      return result;
    }
    return Collections.emptyList();
  }

  /**
   * 
   * @param collectionName
   * @return	returns DBCollection
   */
  private DBCollection getCollection(String collectionName) {
    return mongoTemplate.getCollection(collectionName);
  }
}
