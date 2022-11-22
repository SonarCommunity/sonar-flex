/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2022 SonarSource SA
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
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.flex.FlexCheck;

@Rule(key = "LineLength")
public class LineLengthCheck extends FlexCheck {

  private static final int DEFAULT_MAXIMUM_LINE_LENHGTH = 80;

  @RuleProperty(
    key = "maximumLineLength",
    description = "The maximum authorized line length.",
    defaultValue = "" + DEFAULT_MAXIMUM_LINE_LENHGTH)
  public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENHGTH;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.emptyList();
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    String fileContent = getContext().fileContent();
    String[] lines = fileContent.split("\\r?\\n");

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.length() > maximumLineLength) {
        addIssueAtLine(
          MessageFormat.format("Split this {0} characters long line (which is greater than {1} authorized).", line.length(), maximumLineLength),
          i + 1);
      }
    }
  }
}
