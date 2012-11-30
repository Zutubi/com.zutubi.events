Zutubi Events
=============

Introduction
------------

Zutubi Events is a simple Java library broadcasting and handling events.
Synchronous and asynchronous dispatch are supported, and subscription by event
type.  There's nothing fancy here, just a bit of basic support for publication
and subscription.

Home Page
---------

Zutubi Events has home on the web at:

http://zutubi.com/source/projects/com.zutubi.events/

License
-------

Zutubi Events is licensed under the Apache License, version 2.0.  See the
LICENSE file for details.

Quick Start
-----------

The central interface is EventManager, with an implementation provided in
DefaultEventManager.  Create one or more of these with a chosen dispatcher:

    EventManager manager = DefaultEventManager(new SynchronousDispatcher())

Events can be any class derived from com.zutubi.events.Event:

    public class MyBaseEvent extends Event
    {
      public BuildEvent(Object source)
      {
        super(source);
      }
    }

Listeners implement EventListener, which specifies the events handled by type
and implements the handling method:

    public class MyListener implements EventListener
    {
      public Class[] getHandledEvents()
      {
        return new Class[] { MyBaseEvent.class };
      }

      public void handleEvent(final Event event)
      {
        MyBaseEvent baseEvent = (MyBaseEvent)event;
        doSomething(baseEvent);
      }
    }

they must be registered with the manager:

    manager.register(new MyListener());

And any class can publish events via the manager:

    manager.publish(new MyBaseEvent(this));

The event source is usually set to the instance doing the publication.

Building
--------

A simple Gradle (http://gradle.org/) build is included.  To build the library as
a jar run:

    $ gradle jar

The jar will appear in `build/libs`.  There are no dependencies - the jar
stands alone.  The build has only been tested with Gradle 1.2.

Javadoc
-------

You can generate full Javadoc for the library by running:

    $ gradle javadoc

The documentation will appear in `build/docs/javascript`.

Feedback
--------

Feedback and contributions are welcome!  Please contact:

jason@zutubi.com

or simply fork away!
