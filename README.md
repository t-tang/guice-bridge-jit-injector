guice-bridge-jit-injector
=========================

__Enable JIT bindings in the HK2-Guice bridge__

The HK2 Guice bridge effectively disables the Guice JIT binding function. Upgrading a Guice-ified project from Jersey 1 to Jersey 2 requires manually finding all of the JIT injections and declaring them in the Guice config, the adapter removes the need for this exercise. 

__Using the adapter__

To enable the adapter, simply use the GuiceBridgeJitInjector instead of the regular Guice injector.

```java
      GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    	GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    	//guiceBridge.bridgeGuiceInjector(Guice.createInjector(new GuiceModule()));
    	// Undeclared bindings under org.ttang packages will be bound automatically by Guice
    	guiceBridge.bridgeGuiceInjector(GuiceBridgeJitInjector.create(new GuiceModule(), Package.getPackage("org.ttang")));
```
