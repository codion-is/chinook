module is.codion.framework.demos.chinook.client {
  requires is.codion.swing.common.ui;
  requires is.codion.swing.framework.ui;
  requires is.codion.plugin.imagepanel;
  requires is.codion.swing.plugin.ikonli.foundation;

  requires is.codion.framework.demos.chinook.domain.api;
  requires org.pushingpixels.radiance.substance;
  requires org.pushingpixels.radiance.substance.extras;
  requires jasperreports;

  exports is.codion.framework.demos.chinook.ui;
  exports is.codion.framework.demos.chinook.model;
}