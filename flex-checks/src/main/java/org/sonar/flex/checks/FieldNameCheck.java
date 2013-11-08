/*
 * Sonar Flex Plugin
 * Copyright (C) 2010 SonarSource
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
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.FlexKeyword;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.List;
import java.util.regex.Pattern;


@Rule(
  key = "S116",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class FieldNameCheck extends SquidCheck<LexerlessGrammar> {

  private static final String DEFAULT = "^[_a-z][a-zA-Z0-9]*$";
  private Pattern pattern = null;

  @RuleProperty(
    key = "format",
    defaultValue = DEFAULT)
  String format = DEFAULT;


  @Override
  public void init() {
    subscribeTo(FlexGrammar.CLASS_DEF);
  }

  public void visitFile(AstNode astNode) {
    if (pattern == null) {
      pattern = Pattern.compile(format);
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    List<AstNode> classDirectives = astNode
      .getFirstChild(FlexGrammar.BLOCK)
      .getFirstChild(FlexGrammar.DIRECTIVES)
      .getChildren(FlexGrammar.DIRECTIVE);

    for (AstNode directive : classDirectives) {

      if (isVariableDefinition(directive)) {
        AstNode variableDef = directive
          .getFirstChild(FlexGrammar.ANNOTABLE_DIRECTIVE)
          .getFirstChild(FlexGrammar.VARIABLE_DECLARATION_STATEMENT)
          .getFirstChild(FlexGrammar.VARIABLE_DEF);

        for (AstNode variableBindingNode : variableDef.getFirstChild(FlexGrammar.VARIABLE_BINDING_LIST).getChildren(FlexGrammar.VARIABLE_BINDING)) {
          AstNode identifierNode = variableBindingNode
            .getFirstChild(FlexGrammar.TYPED_IDENTIFIER)
            .getFirstChild(FlexGrammar.IDENTIFIER);

          if (!pattern.matcher(identifierNode.getTokenValue()).matches()) {
            getContext().createLineViolation(this, "Rename this field name to match the regular expression {0}", identifierNode, format);
          }
        }
      }
    }
  }

  private static boolean isVariableDefinition(AstNode directive) {
    if (directive.getFirstChild(FlexGrammar.ANNOTABLE_DIRECTIVE) != null) {
      AstNode annotableDir = directive.getFirstChild(FlexGrammar.ANNOTABLE_DIRECTIVE).getFirstChild();

      return annotableDir.is(FlexGrammar.VARIABLE_DECLARATION_STATEMENT)
        && annotableDir.getFirstChild(FlexGrammar.VARIABLE_DEF).getFirstChild(FlexGrammar.VARIABLE_DEF_KIND).getFirstChild().is(FlexKeyword.VAR);
    }
    return false;
  }
}
