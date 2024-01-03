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
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.flex.FlexCheck;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.checks.utils.Clazz;
import org.sonar.flex.checks.utils.Modifiers;

@Rule(key = "S1820")
public class ClassWithTooManyFieldsCheck extends FlexCheck {


  public static final int DEFAULT_MAX = 20;
  public static final boolean DEFAULT_COUNT_NON_PUBLIC = true;

  @RuleProperty(
    key = "maximumFieldThreshold",
    description = "The maximum number of fields",
    defaultValue = "" + DEFAULT_MAX)
  int maximumFieldThreshold = DEFAULT_MAX;

  @RuleProperty(
    key = "countNonpublicFields",
    description = "Whether or not to include non-public fields in the count",
    defaultValue = "" + DEFAULT_COUNT_NON_PUBLIC,
  type = "BOOLEAN")
  boolean countNonpublicFields = DEFAULT_COUNT_NON_PUBLIC;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(FlexGrammar.CLASS_DEF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    int nbFields = getNumberOfFields(astNode);

    if (nbFields > maximumFieldThreshold) {
      String msg = countNonpublicFields ? String.valueOf(maximumFieldThreshold) : (maximumFieldThreshold + " public");
      String message = MessageFormat.format("Refactor this class so it has no more than {0} fields, rather than the {1} it currently has.", msg, nbFields);
      addIssue(message, astNode);
    }
  }

  private int getNumberOfFields(AstNode classDef) {
    List<AstNode> fields = Clazz.getFields(classDef);
    int nbFields = fields.size();

    if (!countNonpublicFields) {
      nbFields -= getNumberOfNonPublicFields(fields);
    }
    return nbFields;
  }

  private static int getNumberOfNonPublicFields(List<AstNode> fields) {
    int nbNonPublicFields = 0;

    for (AstNode field : fields) {
      if (Modifiers.isNonPublic(Modifiers.getModifiers(field.getPreviousAstNode()))) {
        nbNonPublicFields++;
      }
    }
    return nbNonPublicFields;
  }

}
