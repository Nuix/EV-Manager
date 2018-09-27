package com.nuix.evmanager.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/***
 * A class used to bundle up deltas to the addresses associated with a user's record.
 * @author Jason Wells
 *
 */
public class AddressChangeOrder {
	private SQLUserRecord associatedUserRecord;
	private List<UserAddress> newAddresses = new ArrayList<UserAddress>();
	private List<UserAddress> updatedAddresses = new ArrayList<UserAddress>();
	private List<UserAddress> deletedAddresses = new ArrayList<UserAddress>();
	
	/***
	 * Creates a new instance
	 * @param associatedUserRecord The user record these address changes are associated with
	 * @param newAddresses All the new addresses to be associated with the specified user
	 * @param updatedAddresses All the addresses to be modified which are associated to the specified user
	 * @param deletedAddresses All the addresses to be unassociated from the specified user
	 */
	public AddressChangeOrder(SQLUserRecord associatedUserRecord, List<UserAddress> newAddresses, List<UserAddress> updatedAddresses, List<UserAddress> deletedAddresses){
		this.associatedUserRecord = associatedUserRecord;
		this.newAddresses = newAddresses;
		this.updatedAddresses = updatedAddresses;
		this.deletedAddresses = deletedAddresses;
	}
	
	/***
	 * Generates a summary string listing the deltas
	 * @return A summary string listing the address deltas
	 */
	public String getSummary(){
		StringJoiner result = new StringJoiner("\n");
		if(newAddresses.size() > 0){
			result.add("New Addresses:");
			for(UserAddress address : newAddresses){
				result.add("  "+address.getAddress());
			}
			result.add("");
		}
		if(updatedAddresses.size() > 0){
			result.add("Updated Addresses:");
			for(UserAddress address : updatedAddresses){
				result.add("  "+address.getAddress());
			}
			result.add("");
		}
		if(deletedAddresses.size() > 0){
			result.add("Deleted Addresses:");
			for(UserAddress address : deletedAddresses){
				result.add("  "+address.getAddress());
			}
		}
		return result.toString();
	}
	
	/***
	 * Generates a more terse summary of the deltas.
	 * See {@link AddressChangeOrder#getSummary()} for a more detailed summary.
	 * @return A terse summary of the deltas contained by this instance.
	 */
	public String getLogSummary(){
		return "New: "+newAddresses.size()+
				", Updates: "+updatedAddresses.size()+
				", Deletes: "+deletedAddresses.size();
	}

	/***
	 * Gets the associated user record
	 * @return The associated user record
	 */
	public SQLUserRecord getAssociatedUserRecord() {
		return associatedUserRecord;
	}

	/***
	 * Gets the list of new user addresses
	 * @return A list of the new user addresses
	 */
	public List<UserAddress> getNewAddresses() {
		return newAddresses;
	}

	/***
	 * Gets the list of updated user addresses
	 * @return A list of the updated user addresses
	 */
	public List<UserAddress> getUpdatedAddresses() {
		return updatedAddresses;
	}

	/***
	 * Gets a list of user addresses to be deleted
	 * @return Gets a list of user addresses to be deleted
	 */
	public List<UserAddress> getDeletedAddresses() {
		return deletedAddresses;
	}
}
