module is.codion.framework.demos.chinook.domain {
  requires is.codion.common.db;
  requires is.codion.common.rmi;
  requires is.codion.framework.db.core;
  requires is.codion.framework.db.local;
  requires transitive is.codion.framework.demos.chinook.domain.api;
  requires java.desktop;
  requires jasperreports;

  opens is.codion.framework.demos.chinook.domain;//report resource
  exports is.codion.framework.demos.chinook.domain;
  exports is.codion.framework.demos.chinook.server;

  provides is.codion.framework.domain.Domain
          with is.codion.framework.demos.chinook.domain.ChinookImpl;
  provides is.codion.common.rmi.server.LoginProxy
          with is.codion.framework.demos.chinook.server.ChinookLoginProxy;
}