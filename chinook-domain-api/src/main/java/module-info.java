module is.codion.framework.demos.chinook.domain.api {
  requires is.codion.common.db;
  requires is.codion.framework.db.core;
  requires java.desktop;

  exports is.codion.framework.demos.chinook.domain.api;

  //for accessing default methods in EntityType interfaces
  opens is.codion.framework.demos.chinook.domain.api to is.codion.framework.domain;
}