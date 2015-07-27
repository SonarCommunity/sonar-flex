/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
package org.sonar.plugins.flex;

import org.junit.Test;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.flex.checks.CheckList;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class FlexRuleRepositoryTest {

  @Test
  public void test() {
    FlexRuleRepository ruleRepository = new FlexRuleRepository(new AnnotationRuleParser());
    assertThat(ruleRepository.getKey()).isEqualTo("flex");
    assertThat(ruleRepository.getName()).isEqualTo("SonarQube");
    List<Rule> rules = ruleRepository.createRules();
    assertThat(rules.size()).isEqualTo(CheckList.getChecks().size());
  }

}
