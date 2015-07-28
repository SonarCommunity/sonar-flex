/*
 * Flex Plugin :: Integration Tests
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.it.flex;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.MavenBuild;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  CoverageTest.class,
  FlexIntegrationTest.class,
  FlexSimpleProjectTest.class
})
public class Tests {

  @ClassRule
  public static final Orchestrator ORCHESTRATOR = Orchestrator.builderEnv()
      .addPlugin(FileLocation.of("../../sonar-flex-plugin/target/sonar-flex-plugin.jar"))
      .restoreProfileAtStartup(FileLocation.ofClasspath("/it-profile_flex.xml"))
      .build();

  public static boolean is_sonarqube_after_sonar_5_2() {
    return ORCHESTRATOR.getConfiguration().getSonarVersion().isGreaterThanOrEquals("5.2");
  }

  public static MavenBuild createMavenBuild() {
    return MavenBuild.create();
  }

  public static SonarRunner createSonarRunner() {
    return SonarRunner.create();
  }

}
