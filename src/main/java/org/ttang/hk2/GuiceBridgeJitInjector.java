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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class GuiceBridgeJitInjector implements InvocationHandler {

	public Injector injector;

	private static Method getExistingBindingMethod;
	private static Method getBindingMethod;
	private static Method getTypeLiteralMethod;
	private static Method getRawTypeMethod;

	private Collection<String> packagePrefixes = new ArrayList<String>();
	
	static {
		
		try {
			getExistingBindingMethod =
					Injector.class.getDeclaredMethod("getExistingBinding", new Class[]{Key.class});

			getBindingMethod  =
					Injector.class.getDeclaredMethod("getBinding", new Class[]{Key.class});

			getTypeLiteralMethod =
					Key.class.getDeclaredMethod("getTypeLiteral");

			getRawTypeMethod =
					TypeLiteral.class.getDeclaredMethod("getRawType");

		} catch (NoSuchMethodException e) {
			// Convert to runtime exception, since it's meaningless to continue
			throw new RuntimeException(e);
		}
	}
	
	/*
	 *  Create from module
	 */
	public static Injector create(AbstractModule guiceModule, String... packagePrefixes) {
		return create(Guice.createInjector(guiceModule),packagePrefixes);
	}

	/*
	 *  Create from injector
	 */
	public static Injector create(Injector injector, String... packagePrefixes) {

		return (Injector) Proxy.newProxyInstance(
				Injector.class.getClassLoader(),
				new Class[] {Injector.class},
				new GuiceBridgeJitInjector(injector, packagePrefixes));
	}

	private GuiceBridgeJitInjector(Injector injector, String... packagePrefixes) {
		Collections.<String>addAll(this.packagePrefixes,packagePrefixes);
		this.injector = injector;
	}

	private boolean isInsideTargettedPackage(Class<?> type) {
		String packge = type.getPackage().getName();
		for (String packagePrefix : packagePrefixes) {
			if (packge.startsWith(packagePrefix)) {
				return true;
			}
		}

		return false;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Object result = method.invoke(injector, args);
		
		if (result == null && method.equals(getExistingBindingMethod)) {
			if (isInsideTargettedPackage((Class<?>) getRawTypeMethod.invoke(getTypeLiteralMethod.invoke(args[0])))) {
				return getBindingMethod.invoke(injector,args[0]);
			}
		}

		return result;
	}
}
