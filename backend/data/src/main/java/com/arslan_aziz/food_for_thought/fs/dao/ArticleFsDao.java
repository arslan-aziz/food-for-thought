package com.arslan_aziz.food_for_thought.fs.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.arslan_aziz.food_for_thought.fs.model.Article;

/*
 * Data access object to facilitate file system access for loading and persisting "articles" (i.e. documents).
 */
@Component
public class ArticleFsDao implements ResourceLoaderAware {
	
	private ResourceLoader resourceLoader;
	private Map<String, String> idToPathMap;
	
	@Autowired
	public ArticleFsDao(@Qualifier("RawArticleIdToPathMap") Map<String, String> idToPathMap) {
		this.idToPathMap = idToPathMap;
	}
	
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	
	public Article getArticleFromId(String articleId) throws IOException {
		
		// validate that the id exists in the mapping
		String path = idToPathMap.get(articleId);
		
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			return null;
		}
		
		Resource resource = resourceLoader.getResource("file:" + path);
		
		InputStream in = resource.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		StringBuilder allLines = new StringBuilder();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			allLines.append(line);
		}
		reader.close();
		in.close();
		
		// create Article from resource
		Article article = new Article.ArticleBuilder()
				.filename(resource.getFilename())
				.url(resource.getURL())
				.lastModified(resource.lastModified())
				.contentLength(allLines.length())
				.content(allLines.toString())
				.build();
		
		return article;
	}
}
