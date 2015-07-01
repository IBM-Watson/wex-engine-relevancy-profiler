# Relevancy Tuning in Engine

Relevancy tuning is an iterative approach to adjusting the ranking of search results returned by a query. When talking about relevancy tuning people will often discuss recall and precision.  Recall describes whether documents were actually retrieved by a search regardless of the document's rank (was the document even retrieved?).  Precision refers to how highly the ranking of a particular document for a given search term is compared to other documents. So when talking about relevancy, we want both good recall and good precision.

* Recall -- Was the document retrieved?
* Precision -- Was the retrieved document relevant (relative to the query and user's intent)?

These idea are nice, but to really understand what it means to tune relevancy it helps to think about four broad classes of problems that can negatively impact recall and precision.

1. Poor ranking among documents within a single collection
2. Contention among documents from two or more collections
3. Missing data
4. Language and knowledge issues


## Poor Ranking Among Documents within a single Collection

Documents belonging to the same collection are not ranked as expected.  In this case, tuning strategies focus on the specific collection, however strategies must also consider the holistic effect a change might have across the entire set of searchable content, beyond the collection.  For example, date biasing 1 collection may create an unfair comparison across collections.

```
1. Document A
2. Document B
3. Document C
4. Document D
5. Document E
6. Document F
7. Document G
8. Document H
9. Document I
10. Document J
--------------------- <-- end of first page of results
11. **Highly Valued Document**
```


## Content Among Documents from Two or More collections

Documents belonging to two different collections are not ranked as expected.  In this case, tuning strategies focus on both the specific collections and the interaction between the collections.  One example of this is "source weighting."

Consider the following example.  Assume two collections, when queried individually return the following results.
```
Collection 1
1. **Highly Valued Document**
2. Document A
3. Document B
4. Document C
5. Document D
6. Document E

Collection 2
1. Document Z
2. Document Y
3. Document X
4. Document W
5. Document V
6. Document U
```

But, when those collections are queried simultaneously and the results aggregated, the results can be much different.
```
Aggregation of Collections 1 and 2
1. Document Z
2. Document Y
3. Document X
4. Document W
5. Document V
6. Document U
7. **Highly Valued Document**
8. Document A
9. Document B
10. Document C
```

In this case, the Highly Valued Document has a much lower rank than expected due to contention or weighting between two sources.  Since the High Value document is already the top of its respective collection, you now need to determine whether Collection 1 should be weighted greater on the whole and to what extent.

## Missing Data

Highly relevant data fails to be ranked because it is simply not present.  To improve relevancy in this case the data must be indexed.

## Language and Knowledge issues

Users search with terminology outside of Watson Explorer Engine's context of understanding.  A common example is searching for an acronym when only the expanded acronym is present in documents.  To improve relevancy in this case, Engine's terminology management capabilities must be customized and document metadata configured to provide additional context for search.

For example, what does the three letter acronym "IBM" mean?

* To a hardware engineer, it could mean... *Internal Bench Mark*
* To a regulator, it could mean... *Indian Bureau of Mines*
* To an accountant, it could mean... *International Bank of Miami*
* To a doctor, it could mean... *Irritable Bowel Movement*
* To a software developer, it could mean... *International Business Machines Corporation*

The trick is that all of these interpretations are correct depending on the context of the application. Engine does not know this information by default and every installation could have a different context.  Luckily, knowledge like this can be easily added to Engine.

Generally you'll want to add terminology and knowledge that is tailored to your domain and specific situation.  Acronyms, jargon, synonyms for common or interchangeable terms, and slang are all good candidates for knowledge that could be added to Engine.  Query spelling correction, autocomplete, and lexical analysis at indexing time are other strategies that might help resolve language and knowledge issues.

# Considerations with Analyzing Search Relevancy

Notice that solutions to one problem class often will influence another problem class.  For example, adding new data could create a new "within a collection problem," resolving this problem could create a new "between collections" problem, and this new "between collections" problem could be influenced by "language and knowledge" issues.  It is not uncommon that applying a relevancy strategy will improve relevancy in some ways while hurting relevancy in others. 

One way to evaluate relevancy is to compute metrics relative to a representative ground truth.  Indeed, this is the whole purpose of the relevancy profiler tool.  When analyzing metrics based on any ground truth you should consider both the quantitative and qualitative results.  Metrics alone are just numbers (I got a 5 on metric X... so what?), but root cause analysis can tell you what those metric numbers really mean. 

1. **Qualitative Analysis** – engage subject matter experts to perform searches using a modified configuration and provide feedback on their expectations with respect to the results that were returned.  This is generally done informally and could result in conflicting reports.  For example, one expert reporting that a document is correctly ranked while another reporting that it is incorrectly ranked.  While the feedback is important as a general temperature reading for search (getting warmer vs. getting colder), understanding why the experts expected certain documents is essential.  This deeper exploration yields insights into how users think when searching, which in turn leads to specific tuning strategies.
2. **Quantitatively** – perform a statistical analysis of searched ranked documents based on a set of queries and high-value documents provided by subject matter experts.  This strategy is still subject to expert bias, however assuming that the set of queries used to create the ground truth is generally representative of users’ expectations, metrics can be evaluated as modifications are made over time to show trends beyond the typical "point in time" focus typical when evaluating relevancy manually.
