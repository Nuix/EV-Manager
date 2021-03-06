/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMER’S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMER’S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN “AS IS” AND “AS AVAILABLE” BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMER’S PURPOSES.
 */
package com.nuix.evmanager;

import java.lang.Package;
import java.util.regex.Pattern;

/***
 * Assists in representing a Nuix version in object form to assist with comparing two versions.  This allows for things such as
 * only executing chunks of code if the version meets a requirement.<br>
 * Ruby example:
 * <pre>
 * {@code
 * current_version = NuixVersion.new(NUIX_VERSION)
 * if current_version.isLessThan("6.0")
 *     puts "Sorry your version of Nuix is below the minimum required version of 6.0"
 *     exit 1
 * end
 * }
 * </pre>
 * @author JasonWells
 *
 */
public class NuixVersion implements Comparable<NuixVersion> {
	private static Pattern previewVersionInfoRemovalPattern = Pattern.compile("[^0-9\\.].*$");
	
	private int major = 0;
	private int minor = 0;
	private int build = 0;
	
	/***
	 * Creates a new instance defaulting to version 0.0.0
	 */
	public NuixVersion(){
		this(0,0,0);
	}
	
	/***
	 * Creates a new instance using the provided major version: major.0.0
	 * @param majorVersion The major version number
	 */
	public NuixVersion(int majorVersion){
		this(majorVersion,0,0);
	}
	/***
	 * Creates a new instance using the provided major and minor versions: major.minor.0
	 * @param majorVersion The major version number
	 * @param minorVersion The minor version number
	 */
	public NuixVersion(int majorVersion, int minorVersion){
		this(majorVersion,minorVersion,0);
	}
	/***
	 * Creates a new instance using the provided major, minor and build versions: major.minor.build
	 * @param majorVersion The major version number
	 * @param minorVersion The minor version number
	 * @param buildVersion The build version number
	 */
	public NuixVersion(int majorVersion, int minorVersion, int buildVersion){
		major = majorVersion;
		minor = minorVersion;
		build = buildVersion;
	}
	
	/***
	 * Parses a version string into a NuixVersion instance.  Supports values such as: 6, 6.2, 6.2.0, 6.2.1-preview6 <br>
	 * When providing a version string such as "6.2.1-preview6", "-preview6" will be trimmed off before parsing.
	 * @param versionString The version string to parse.
	 * @return A NuixVersion instance representing the supplied version string, if there is an error parsing the provided value will return
	 * an instance representing 100.0.0
	 */
	public static NuixVersion parse(String versionString){
		try {
			String[] versionParts = NuixVersion.previewVersionInfoRemovalPattern.matcher(versionString.trim()).replaceAll("").split("\\.");
			int[] versionPartInts = new int[versionParts.length];
			for(int i=0;i<versionParts.length;i++){
				versionPartInts[i] = Integer.parseInt(versionParts[i]);
			}
			switch(versionParts.length){
				case 1:
					return new NuixVersion(versionPartInts[0]);
				case 2:
					return new NuixVersion(versionPartInts[0],versionPartInts[1]);
				case 3:
					return new NuixVersion(versionPartInts[0],versionPartInts[1],versionPartInts[2]);
				default:
					return new NuixVersion();
			}
		}catch(Exception exc){
			System.out.println("Error while parsing version: "+versionString);
			System.out.println("Pretending version is 100.0.0");
			return new NuixVersion(100,0,0);
		}
	}
	
	/***
	 * Gets the determined major portion of this version instance (X.0.0)
	 * @return The determined major portion of version
	 */
	public int getMajor() {
		return major;
	}
	/***
	 * Sets the major portion of this version instance (X.0.0)
	 * @param major The major version value
	 */
	public void setMajor(int major) {
		this.major = major;
	}
	/***
	 * Gets the determined minor portion of this version instance (0.X.0)
	 * @return The determined minor portion of version
	 */
	public int getMinor() {
		return minor;
	}
	/***
	 * Sets the minor portion of this version instance (0.X.0)
	 * @param minor The minor version value
	 */
	public void setMinor(int minor) {
		this.minor = minor;
	}
	/***
	 * Gets the determined build portion of this version instance (0.0.X)
	 * @return The determined build portion of version
	 */
	public int getBuild() {
		return build;
	}
	/***
	 * Sets the build portion of this version instance (0.0.X)
	 * @param build The build version value
	 */
	public void setBuild(int build) {
		this.build = build;
	}
	
	/***
	 * Attempts to determine current Nuix version by inspecting Nuix packages.  It is preffered to instead use
	 * {@link #parse(String)} when version string is available, such as in Ruby using constant NUIX_VERSION.
	 * @return Best guess at current Nuix version based on package inspection.
	 */
	public static NuixVersion getCurrent(){
		String versionString = "0.0.0";
		for(Package p : Package.getPackages()){
			if(p.getName().matches("com\\.nuix\\..*")){
				versionString = p.getImplementationVersion();
				break;
			}
		}
		return NuixVersion.parse(versionString);
	}

	/***
	 * Determines whether another instance's version is less than this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is a lower version, false otherwise
	 */
	public boolean isLessThan(NuixVersion other){
		return this.compareTo(other) < 0;
	}
	/***
	 * Determines whether another instance's version is greater than or equal to this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is greater than or equal to this instance, false otherwise
	 */
	public boolean isAtLeast(NuixVersion other){
		return this.compareTo(other) >= 0;
	}
	/***
	 * Determines whether another instance's version is greater than this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is greater than this instance, false otherwise
	 */
	public boolean isGreaterThan(NuixVersion other){
		return this.compareTo(other) > 0;
	}
	/***
	 * Determines whether another instance's version is greater than this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is greater than this instance, false otherwise
	 */
	public boolean isGreaterThan(String other){
		return this.compareTo(NuixVersion.parse(other)) > 0;
	}
	/***
	 * Determines whether another instance's version is equal to this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is greater than this instance, false otherwise
	 */
	public boolean isEqualTo(NuixVersion other){
		return this.compareTo(other) == 0;
	}
	/***
	 * Determines whether another instance's version is equal to this instance
	 * @param other The other instance to compare against
	 * @return True if the other instance is equal to this instance (major, minor and release are the same), false otherwise
	 */
	public boolean isEqualTo(String other){
		return this.compareTo(NuixVersion.parse(other)) == 0;
	}
	/***
	 * Determines whether another instance's version is less than this instance
	 * @param other A version string to compare against
	 * @return True if the other instance is a lower version, false otherwise
	 */
	public boolean isLessThan(String other){
		return isLessThan(parse(other));
	}
	/***
	 * Determines whether another instance's version is greater than or equal to this instance
	 * @param other A version string to compare against
	 * @return True if the other instance is greater than or equal to this instance, false otherwise
	 */
	public boolean isAtLeast(String other){
		return isAtLeast(parse(other));
	}
	
	/***
	 * Provides comparison logic when comparing two instances.
	 */
	@Override
	public int compareTo(NuixVersion other) {
		if(this.major == other.major){
			if(this.minor == other.minor){
				return Integer.compare(this.build, other.build);
			}
			else{
				return Integer.compare(this.minor, other.minor);
			}
		}
		else{
			return Integer.compare(this.major, other.major);
		}
		
	}
	
	/***
	 * Converts this instance back to a version string such as: 6.2.1234
	 */
	@Override
	public String toString(){
		return Integer.toString(this.major) + "." +
				Integer.toString(this.minor) + "." +
				Integer.toString(this.build);
	}
}
