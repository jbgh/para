/*
 * Copyright 2013-2014 Erudika. http://erudika.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For issues and patches go to: https://github.com/erudika
 */
package com.erudika.para.search;

import com.erudika.para.utils.Pager;
import com.erudika.para.core.ParaObject;
import java.util.List;
import java.util.Map;

/**
 * The core search interface. Does indexing and searching for all domain objects.
 * @author Alex Bogdanovski [alex@erudika.com]
 */
public interface Search {

	/////////////////////////////////////////////
	//			   CORE METHODS
	/////////////////////////////////////////////

	/**
	 * Indexes an object. Only fields marked with {@link com.erudika.para.annotations.Stored} are indexed.
	 * @param so the domain object the object to index
	 */
	void index(ParaObject so);

	/**
	 * Indexes an object. Only fields marked with {@link com.erudika.para.annotations.Stored} are indexed.
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param so the domain object
	 */
	void index(String appid, ParaObject so);

	/**
	 * Indexes an object. Only fields marked with {@link com.erudika.para.annotations.Stored} are indexed.
	 * Automatically removes the object from the index after TTL milliseconds.
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param so the domain object
	 * @param ttl time to live in milliseconds before the object is removed from the index.
	 */
	void index(String appid, ParaObject so, long ttl);

	/**
	 * Removes an object from the index.
	 * @param so the domain object
	 */
	void unindex(ParaObject so);

	/**
	 * Removes an object from the index.
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param so the domain object
	 */
	void unindex(String appid, ParaObject so);

	/**
	 * Indexes multiple objects in a batch operation.
	 * @param <P> type of the object
	 * @param objects a list of objects
	 */
	<P extends ParaObject> void indexAll(List<P> objects);

	/**
	 * Indexes multiple objects in a batch operation.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param objects a list of objects
	 */
	<P extends ParaObject> void indexAll(String appid, List<P> objects);

	/**
	 * Removes multiple objects from the index in a batch operation.
	 * @param <P> type of the object
	 * @param objects a list of objects
	 */
	<P extends ParaObject> void unindexAll(List<P> objects);

	/**
	 * Removes multiple objects from the index in a batch operation.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param objects a list of objects
	 */
	<P extends ParaObject> void unindexAll(String appid, List<P> objects);

	/////////////////////////////////////////////
	//			    SEARCH METHODS
	/////////////////////////////////////////////

	/**
	 * Simple id search.
	 * @param <P> type of the object
	 * @param id the id
	 * @return the object if found or null
	 */
	<P extends ParaObject> P findById(String id);

	/**
	 * Simple id search.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param id the id
	 * @return the object if found or null
	 */
	<P extends ParaObject> P findById(String appid, String id);

	/**
	 * Search for {@link com.erudika.para.core.Address} objects in a radius of X km from a given point.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param query the query string
	 * @param radius the radius of the search circle
	 * @param lat latitude
	 * @param lng longitude
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findNearby(String type, String query, int radius, double lat, double lng, Pager... pager);

	/**
	 * Search for {@link com.erudika.para.core.Address} objects in a radius of X km from a given point.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param query the query string
	 * @param radius the radius of the search circle
	 * @param lat latitude
	 * @param lng longitude
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findNearby(String appid, String type, String query, int radius, double lat, double lng, Pager... pager);

	/**
	 * Searches for objects that have a property which value starts with a given prefix.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param prefix the prefix
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findPrefix(String type, String field, String prefix, Pager... pager);

	/**
	 * Searches for objects that have a property which value starts with a given prefix.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param prefix the prefix
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findPrefix(String appid, String type, String field, String prefix, Pager... pager);

	/**
	 * Simple query string search. This is the basic search method.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param query the query string
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findQuery(String type, String query, Pager... pager);

	/**
	 * Simple query string search. This is the basic search method.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param query the query string
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findQuery(String appid, String type, String query, Pager... pager);

	/**
	 * Searches for objects that have similar property values to a given text. A "find like this" query.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param filterKey exclude an object with this key from the results (optional)
	 * @param fields a list of property names
	 * @param liketext text to compare to
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findSimilar(String type, String filterKey, String[] fields, String liketext, Pager... pager);

	/**
	 * Searches for objects that have similar property values to a given text. A "find like this" query.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param filterKey exclude an object with this key from the results (optional)
	 * @param fields a list of property names
	 * @param liketext text to compare to
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findSimilar(String appid, String type, String filterKey, String[] fields, String liketext, Pager... pager);

	/**
	 * Searches for objects tagged with one or more tags.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param tags the list of tags
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTagged(String type, String[] tags, Pager... pager);

	/**
	 * Searches for objects tagged with one or more tags.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param tags the list of tags
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTagged(String appid, String type, String[] tags, Pager... pager);

	/**
	 * Searches for {@link com.erudika.para.core.Tag} objects.
	 * This method might be deprecated in the future.
	 * @param <P> type of the object
	 * @param keyword the tag keyword to search for
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTags(String keyword, Pager... pager);

	/**
	 * Searches for {@link com.erudika.para.core.Tag} objects.
	 * This method might be deprecated in the future.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param keyword the tag keyword to search for
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTags(String appid, String keyword, Pager... pager);

	/**
	 * Searches for objects having a property value that is in list of possible values.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param terms a list of terms (property values)
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTermInList(String type, String field, List<?> terms, Pager... pager);

	/**
	 * Searches for objects having a property value that is in list of possible values.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param terms a list of terms (property values)
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTermInList(String appid, String type, String field, List<?> terms, Pager... pager);

	/**
	 * Searches for objects that have properties matching some given values. A terms query.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param terms a map of fields (property names) to terms (property values)
	 * @param matchAll match all terms. If true - AND search, if false - OR search
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTerms(String type, Map<String, ?> terms, boolean matchAll, Pager... pager);

	/**
	 * Searches for objects that have properties matching some given values. A terms query.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param terms a map of fields (property names) to terms (property values)
	 * @param matchAll match all terms. If true - AND search, if false - OR search
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findTerms(String appid, String type, Map<String, ?> terms, boolean matchAll, Pager... pager);

	/**
	 * Searches for objects that have a property with a value matching a wildcard query.
	 * @param <P> type of the object
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param wildcard wildcard query string. For example "cat*".
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findWildcard(String type, String field, String wildcard, Pager... pager);

	/**
	 * Searches for objects that have a property with a value matching a wildcard query.
	 * @param <P> type of the object
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param field the property name of an object
	 * @param wildcard wildcard query string. For example "cat*".
	 * @param pager a {@link com.erudika.para.utils.Pager}
	 * @return a list of objects found
	 */
	<P extends ParaObject> List<P> findWildcard(String appid, String type, String field, String wildcard, Pager... pager);

	/////////////////////////////////////////////
	//			  COUNTING METHODS
	/////////////////////////////////////////////

	/**
	 * Counts indexed objects.
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @return the number of results found
	 */
	Long getCount(String type);

	/**
	 * Counts indexed objects.
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @return the number of results found
	 */
	Long getCount(String appid, String type);

	/**
	 * Counts indexed objects matching a set of terms/values.
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param terms a list of terms (property values)
	 * @return the number of results found
	 */
	Long getCount(String type, Map<String, ?> terms);

	/**
	 * Counts indexed objects matching a set of terms/values.
	 * @param appid name of the {@link com.erudika.para.core.App}
	 * @param type the type of object to search for. See {@link com.erudika.para.core.ParaObject#getType()}
	 * @param terms a map of fields (property names) to terms (property values)
	 * @return the number of results found
	 */
	Long getCount(String appid, String type, Map<String, ?> terms);

}
