/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package org.ttang.hk2;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;

/**
 * This adapter extends the capabilities of the HK2-Guice bridge to include Guice JIT bindings
 * this removes the need to declare bindings for all Guice components which are to be injected
 * into Jersey.
 * 
 * To enable the adapter, simply use the GuiceBridgeJitInjector instead of the regular Guice injector.
 * 
 *    guiceBridge.bridgeGuiceInjector(GuiceBridgeJitInjector.create(new GuiceModule(), Package.getPackage("com.company.yourapp")));
 * 
 */
public class GuiceBridgeJitInjector implements Injector {

	private Injector guiceInjector;
	private Collection<String> packageNames;

	/*
	 *  Creates a new adapter, this is a convenience method.
	 */
	public static GuiceBridgeJitInjector create(AbstractModule guiceModule, Collection<String> packagePrefixes) {
		return new GuiceBridgeJitInjector(Guice.createInjector(guiceModule),packagePrefixes);
	}

	/*
	 *  Convenience method for creation.
	 */
	public static GuiceBridgeJitInjector create (AbstractModule guiceModule, String... packagePrefixes) {
		return new GuiceBridgeJitInjector(Guice.createInjector(guiceModule),Arrays.asList(packagePrefixes));
	}

	/*
	 *  Convenience method for creation.
	 */
	public static GuiceBridgeJitInjector create (Injector guiceInjector, String... packagePrefixes) {
		return new GuiceBridgeJitInjector(guiceInjector, Arrays.asList(packagePrefixes));
	}

	public GuiceBridgeJitInjector(Injector guiceInjector, Collection<String> packagePrefixes) {

		this.guiceInjector = guiceInjector;
		this.packageNames = new ArrayList<String>(packagePrefixes.size());

		for (String packagePrefix : packagePrefixes) {
			packageNames.add(packagePrefix);
		}

	}

	private boolean isInsideTargettedPackage(Class<?> type) {
		String packge = type.getPackage().getName();
		for (String packageName : packageNames) {
			if (packge.startsWith(packageName)) {
				return true;
			}
		}

		return false;
	}

	public Injector createChildInjector(Iterable<? extends Module> arg0) {
		return guiceInjector.createChildInjector(arg0);
	}

	public Injector createChildInjector(Module... arg0) {
		return guiceInjector.createChildInjector(arg0);
	}

	public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> arg0) {
		return guiceInjector.findBindingsByType(arg0);
	}

	public Map<Key<?>, Binding<?>> getAllBindings() {
		return guiceInjector.getAllBindings();
	}

	public <T> Binding<T> getBinding(Class<T> arg0) {
		return guiceInjector.getBinding(arg0);
	}

	public <T> Binding<T> getBinding(Key<T> arg0) {
		return guiceInjector.getBinding(arg0);
	}

	public Map<Key<?>, Binding<?>> getBindings() {
		return guiceInjector.getBindings();
	}

	public <T> Binding<T> getExistingBinding(Key<T> arg0) {

		Binding<T> binding = guiceInjector.getExistingBinding(arg0);
		
		if (binding != null) {
			return binding;
		}

		if (isInsideTargettedPackage(arg0.getTypeLiteral().getRawType())) {
				return guiceInjector.getBinding(arg0);
		}

		return null;
	}

	public <T> T getInstance(Class<T> arg0) {
		return guiceInjector.getInstance(arg0);
	}

	public <T> T getInstance(Key<T> arg0) {
		return guiceInjector.getInstance(arg0);
	}

	public <T> MembersInjector<T> getMembersInjector(Class<T> arg0) {
		return guiceInjector.getMembersInjector(arg0);
	}

	public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> arg0) {
		return guiceInjector.getMembersInjector(arg0);
	}

	public Injector getParent() {
		return guiceInjector.getParent();
	}

	public <T> Provider<T> getProvider(Class<T> arg0) {
		return guiceInjector.getProvider(arg0);
	}

	public <T> Provider<T> getProvider(Key<T> arg0) {
		return guiceInjector.getProvider(arg0);
	}

	public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
		return guiceInjector.getScopeBindings();
	}

	public Set<TypeConverterBinding> getTypeConverterBindings() {
		return guiceInjector.getTypeConverterBindings();
	}

	public void injectMembers(Object arg0) {
		guiceInjector.injectMembers(arg0);
	}
}
