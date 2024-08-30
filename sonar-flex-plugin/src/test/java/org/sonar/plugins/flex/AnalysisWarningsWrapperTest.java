/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.flex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnalysisWarningsWrapperTest {

  @Test
  void test() {
    List<String> warnings = new ArrayList<>();
    AnalysisWarningsWrapper wrapper = new AnalysisWarningsWrapper(warnings::add);
    wrapper.addUnique("test");
    assertThat(warnings).containsExactly("test");
  }

  @Test
  void test_null() {
    try {
      AnalysisWarningsWrapper wrapper = new AnalysisWarningsWrapper(null);
      wrapper.addUnique("test");
    } catch (Exception e) {
      fail("No exception should be thrown");
    }
  }
}
