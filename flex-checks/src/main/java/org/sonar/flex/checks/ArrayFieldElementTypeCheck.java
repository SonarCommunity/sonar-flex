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
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.checks.utils.Clazz;
import org.sonar.flex.checks.utils.MetadataTag;
import org.sonar.flex.checks.utils.Variable;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(key = "S1469",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class ArrayFieldElementTypeCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(FlexGrammar.CLASS_DEF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    for (AstNode directive : Clazz.getDirectives(astNode)) {

      if (Variable.isVariable(directive)) {
        AstNode varBindingList = directive
          .getFirstChild(FlexGrammar.ANNOTABLE_DIRECTIVE)
          .getFirstChild(FlexGrammar.VARIABLE_DECLARATION_STATEMENT)
          .getFirstChild(FlexGrammar.VARIABLE_DEF)
          .getFirstChild(FlexGrammar.VARIABLE_BINDING_LIST);

        for (AstNode varBinding : varBindingList.getChildren(FlexGrammar.VARIABLE_BINDING)) {

          if (!hasInitialisation(varBinding) && isArray(varBinding) && !hasArrayTypeTag(directive)) {
            getContext().createLineViolation(this, "Define the element type for this ''{0}'' array", varBinding,
              varBinding.getFirstChild(FlexGrammar.TYPED_IDENTIFIER).getFirstChild(FlexGrammar.IDENTIFIER).getTokenValue());
          }
        }
      }

    }
  }

  private static boolean hasInitialisation(AstNode varBinding) {
    return varBinding.getFirstChild(FlexGrammar.VARIABLE_INITIALISATION) != null;
  }

  private static boolean isArray(AstNode varBinding) {
    AstNode typeExpr = varBinding.getFirstChild(FlexGrammar.TYPED_IDENTIFIER).getFirstChild(FlexGrammar.TYPE_EXPR);

    return typeExpr != null
      && typeExpr.getNumberOfChildren() == 1
      && "Array".equals(typeExpr.getFirstChild().getTokenValue());
  }

  private static boolean hasArrayTypeTag(AstNode directive) {
    AstNode previousDirective = directive.getPreviousAstNode();

    while (previousDirective != null && MetadataTag.isMetadataTag(previousDirective)) {
      if (MetadataTag.isTag(previousDirective.getFirstChild().getFirstChild(FlexGrammar.METADATA_STATEMENT), "ArrayElementType")) {
        return true;
      }
      previousDirective = previousDirective.getPreviousAstNode();
    }
    return false;
  }
}
