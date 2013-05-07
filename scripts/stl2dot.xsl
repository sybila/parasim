<?xml version="1.0" encoding="UTF-8" ?>
<!-- Transforms STL* formula (in XML) to dot file which can be compiled by GraphViz to generate the formula syntax tree. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:f="http://www.sybila.org/parasim/stl-formula">
	<xsl:output method="text"/>
	<xsl:template match="f:interval">[<xsl:apply-templates select="f:lower"/>,<xsl:apply-templates select="f:upper"/>]</xsl:template>
	<xsl:template name="subformulae">
		<xsl:variable name="id" select="generate-id(.)"/>
		<xsl:for-each select="f:and|f:or|not|f:globally|f:future|f:until|f:freeze|f:predicate">
			<xsl:apply-templates select="."/>
			<xsl:value-of select="$id"/> -&gt; <xsl:value-of select="generate-id(.)"/><xsl:text/><xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="/">
		<xsl:apply-templates match="f:formula"/>
	</xsl:template>
	<xsl:template match="f:formula">
		digraph formula {
		<xsl:value-of select="generate-id(.)"/> [shape="none",label=""]
		<xsl:call-template name="subformulae"/>
		}
	</xsl:template>
	<xsl:template match="f:and">
		<xsl:value-of select="generate-id(.)"/> [label="AND"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:or">
		<xsl:value-of select="generate-id(.)"/> [label="OR"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:not">
		<xsl:value-of select="generate-id(.)"/> [label="NOT"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:globally">
		<xsl:value-of select="generate-id(.)"/> [label="G<xsl:apply-templates select="f:interval"/>"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:future">
		<xsl:value-of select="generate-id(.)"/> [label="F<xsl:apply-templates select="f:interval"/>"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:until">
		<xsl:value-of select="generate-id(.)"/> [label="U<xsl:apply-templates select="f:interval"/>"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:freeze">
		<xsl:value-of select="generate-id(.)"/> [label="*<xsl:value-of select="@index"/>"]
		<xsl:call-template name="subformulae"/>
	</xsl:template>
	<xsl:template match="f:predicate">
		<xsl:value-of select="generate-id(.)"/> [label="<xsl:apply-templates select="f:variable"/><xsl:text> </xsl:text><xsl:apply-templates select="f:greater|f:lesser|f:equals"/><xsl:text> </xsl:text><xsl:value-of select="f:value"/>"]
	</xsl:template>
	<xsl:template match="f:variable">
		<xsl:choose>
			<xsl:when test="@multiplier=1">+</xsl:when>
			<xsl:when test="@multiplier=-1">-</xsl:when>
			<xsl:when test="@multiplier&gt;0">+<xsl:value-of select="@multiplier"/></xsl:when>
			<xsl:when test="@multiplier&lt;0"><xsl:value-of select="@multiplier"/></xsl:when>
		</xsl:choose>
		<xsl:value-of select="."/>
		<xsl:if test="@frozen"><xsl:if test="not(@frozen=0)">[<xsl:value-of select="@frozen"/>]</xsl:if></xsl:if>
	</xsl:template>
	<xsl:template match="f:greater">&gt;</xsl:template>
	<xsl:template match="f:lesser">&lt;</xsl:template>
	<xsl:template match="f:equals">=</xsl:template>
</xsl:stylesheet>
