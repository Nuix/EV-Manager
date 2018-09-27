package com.nuix.evmanager.data;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

/***
 * Virtualizes access to pages of results to List like behavior.  Uses threading to predictively fetch additional pages
 * around the page being actively worked with to make access to the collection feel more fluid and without constant database access pauses.
 * @author Jason Wells
 *
 * @param <T> The type of object this collection will hold
 */
public abstract class VirtualizedRecordCollection<T> implements AutoCloseable{
	protected int pageSize = 100;
	protected static int prefetchedPages = 8;
	
	public abstract int size() throws Exception;
	protected abstract void loadPage(int pageNumber) throws Exception;
	
	protected ConcurrentMap<Integer,List<T>> pages = null;
	protected LinkedBlockingDeque<Integer> backgroundPageFetchQueue = null;
	Thread backgroundFetchThread;
	
	public VirtualizedRecordCollection(){
		pages = new ConcurrentHashMap<Integer,List<T>>();
		backgroundPageFetchQueue = new LinkedBlockingDeque<Integer>();
		
		backgroundFetchThread = new Thread(()->{
			while(true){
				try {
					Integer pageNumber = backgroundPageFetchQueue.take();
					if(pageNumber == -1337){
						System.out.println("Background fetcher shutting down");
						break;
					}
					if(isValidPage(pageNumber) && !pages.containsKey(pageNumber)){
						System.out.println("Background fetching page: "+pageNumber);
						loadPage(pageNumber);	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		backgroundFetchThread.setDaemon(true);
		backgroundFetchThread.start();
	}
	
	/***
	 * Determines whether a given page is valid given the number of elements
	 * @param pageNumber The page number to test the validity of
	 * @return True or false as to whether the given page is valid
	 * @throws Exception Thrown if {@link #size()} throws an Exception
	 */
	protected boolean isValidPage(int pageNumber) throws Exception{
		int minPage = 0;
		int maxPage = ((int)Math.ceil(((double)size()) / ((double)pageSize))) - 1;
		
		if(pageNumber < minPage) return false;
		if(pageNumber > maxPage) return false;
		return true;
	}
	
	/***
	 * Gets a particular element from this collection.  Potentially fetching a new page of data from the data source or
	 * returning the appropriate element from an already fetched page of data.
	 * @param index The index of the element to return
	 * @return The requested element
	 * @throw IndexOutOfBoundsException If the specified index is outside the range of elements represented by this instance
	 * @throws Exception Thrown if other code throws an exception
	 */
	@SuppressWarnings("static-access")
	public T get(int index) throws IndexOutOfBoundsException, Exception{
		if(index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index "+index+" is invalid, valid values are 0 >= index < "+size());
		int pageNumber = index / pageSize;
		
		backgroundPageFetchQueue.push(pageNumber);
		
		for (int i = 0-prefetchedPages/2; i <= prefetchedPages/2; i++) {
			if(i == 0) continue;
			if(pageNumber+i < 0) continue;
			backgroundPageFetchQueue.add(pageNumber+i);
		}
		
		//Wait for requested page to appear
		while(true){
			if (pages.containsKey(pageNumber)) break;
			Thread.currentThread().sleep(10);
		}
		return pages.get(pageNumber).get(index - (pageNumber * pageSize));
	}
	
	/***
	 * Signals the background thread performing pre-fetch of pages to shut down.
	 * @throws IOException If there is an exception thrown while shut down signal pushed to page pre-fetch queue 
	 */
	@Override
	public void close() throws IOException {
		//Signal background fetch thread to shutdown
		backgroundPageFetchQueue.push(-1337);
	}
}
