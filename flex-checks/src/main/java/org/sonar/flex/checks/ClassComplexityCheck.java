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
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.api.FlexMetric;
import org.sonar.squidbridge.api.SourceClass;
import org.sonar.squidbridge.checks.ChecksHelper;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "ClassComplexity",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class ClassComplexityCheck extends SquidCheck<LexerlessGrammar> {

  private static final int DEFAULT_MAXIMUM_CLASS_COMPLEXITY_THRESHOLD = 80;

  @RuleProperty(
    key = "maximumClassComplexityThreshold",
    defaultValue = "" + DEFAULT_MAXIMUM_CLASS_COMPLEXITY_THRESHOLD)
  private int maximumClassComplexityThreshold = DEFAULT_MAXIMUM_CLASS_COMPLEXITY_THRESHOLD;

  @Override
  public void init() {
    subscribeTo(FlexGrammar.CLASS_DEF);
  }

  @Override
  public void leaveNode(AstNode node) {
    SourceClass sourceClass = (SourceClass) getContext().peekSourceCode();
    int complexity = ChecksHelper.getRecursiveMeasureInt(sourceClass, FlexMetric.COMPLEXITY);
    if (complexity > maximumClassComplexityThreshold) {
      getContext().createLineViolation(this,
        "Class has a complexity of {0,number,integer} which is greater than {1,number,integer} authorized.",
        node,
        complexity,
        maximumClassComplexityThreshold);
    }
  }

  public void setMaximumClassComplexityThreshold(int threshold) {
    this.maximumClassComplexityThreshold = threshold;
  }

}
