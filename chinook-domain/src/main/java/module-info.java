module is.codion.framework.demos.chinook.domain {
  requires is.codion.common.db;
  requires is.codion.common.rmi;
  requires is.codion.framework.db.core;
  requires is.codion.framework.db.local;
  requires is.codion.framework.demos.chinook.domain.api;
  requires java.desktop;

  exports is.codion.framework.demos.chinook.domain;
  exports is.codion.framework.demos.chinook.server;

  provides is.codion.framework.domain.Domain
          with is.codion.framework.demos.chinook.domain.ChinookImpl;
  provides is.codion.common.rmi.server.LoginProxy
          with is.codion.framework.demos.chinook.server.ChinookLoginProxy;
}