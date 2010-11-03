/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devsurf.injection.guice.scanner;


public class PackageFilter {
	private String _package;
	private boolean deep;
	
	public PackageFilter(String p, boolean deeper){
		_package = p;
		deep = deeper;
	}
	
	public String getPackage(){
		return _package;
	}
	
	public boolean deep() {
		return deep;
	}
	
	public static PackageFilter create(Class<?> clazz){
		return new PackageFilter(clazz.getPackage().getName(), true);
	}
	public static PackageFilter create(Class<?> clazz, boolean deeper){
		return new PackageFilter(clazz.getPackage().getName(), deeper);
	}
	public static PackageFilter create(Package p){
		return new PackageFilter(p.getName(), true);
	}
	public static PackageFilter create(Package p, boolean deeper){
		return new PackageFilter(p.getName(), deeper);
	}
	public static PackageFilter create(String p){
		return new PackageFilter(p, true);
	}
	public static PackageFilter create(String p, boolean deeper){
		return new PackageFilter(p, deeper);
	}
}
