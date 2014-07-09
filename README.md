guice-bridge-jit-injector
=========================

__Get Guice to work smoothly with Jersey-HK2__

If are using Guice with __Jersey 2__, you will almost certainly need to inject Guice components into Jersey components. Unlike regular Guice, any Guice components injected into Jersey will need a binding declared in the Guice config. This adapter extends the capabilities of the bridge to include Guice JIT bindings and removes the need for pre-declaration.

__Using the adapter__

To enable the adapter, simply use the GuiceBridgeJitInjector instead of the regular Guice injector.

```java
    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    //guiceBridge.bridgeGuiceInjector(Guice.createInjector(new GuiceModule()));
    // Undeclared bindings under org.ttang packages will be bound automatically by Guice
    guiceBridge.bridgeGuiceInjector(GuiceBridgeJitInjector.create(new GuiceModule(), "org.company.app"));
```
In a real application you will probably want to use this same Injector instance for your regular code as well, so you could pass the Guice injector to the Jersey ResourceConfig through a ServletContext attribute and use it to initialize the bridge.

__The package prefix requirement__

Requiring the package prefixes to be declared is needed to maintain compatibility with Jersey. Some of the Jersey internal classes fall through to Guice, unless these are filtered out, Jersey may fail because Guice can't honor the HK2 annotations.

__Notes__

This project has been submitted to the HK2 team to be considered for inclusion in the Guice bridge.
