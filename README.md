guice-bridge-jit-injector
=========================

__Get Guice to work nicely with HK2__

Upgrading a Guice-ified project to Jersey 2 requires manually finding all of the JIT injections and declaring them in the Guice config, this adapter removes the need for that exercise.

__Using the adapter__

To enable the adapter, simply use the GuiceBridgeJitInjector instead of the regular Guice injector.

```java
      GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    	GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    	//guiceBridge.bridgeGuiceInjector(Guice.createInjector(new GuiceModule()));
    	// Undeclared bindings under org.ttang packages will be bound automatically by Guice
    	guiceBridge.bridgeGuiceInjector(GuiceBridgeJitInjector.create(new GuiceModule(), Package.getPackage("org.ttang")));
```

__The package requirement__

Requiring the packages to be declared is needed to maintain compatibility with Jersey. Some of the Jersey internal classes fall through to Guice, unless these are filtered out, Jersey may fail because Guice can't honor the HK2 annotations.

