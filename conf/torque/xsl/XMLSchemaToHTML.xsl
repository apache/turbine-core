<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
  <xsl:output method="html" indent="yes"/>
    <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="database">
    <html>
    <head>
    <title>
        Database Schema
    </title>
    </head>
    <body  bgcolor="#ffffff" text="#000000">
	   <xsl:apply-templates select="table"/>
    </body>
    </html>
    </xsl:template>

    <xsl:template match="table">
      <table>
        <tr>
          <td bgcolor="#000000">
            <font color="white" face="Lucida,Verdana,Helvetica,Arial">
              <xsl:apply-templates select="@name"/>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <table cellspacing="0" cellpadding="2">
              <tr>
                <td>Column</td>
                <td>Type</td>
                <td>Size</td>
              </tr>
              <font color="white" face="Lucida,Verdana,Helvetica,Arial">
                <xsl:apply-templates select="column"/>
              </font>
	       </table>
          </td>
        </tr>
      </table>
      <p/>
    </xsl:template>

    <xsl:template match="column">
      <tr>
        <td bgcolor="#a0ddf0">
          <xsl:value-of select="@name"/>
        </td>
        <td bgcolor="#a0ddf0">
          <xsl:value-of select="@type"/>
        </td>
        <td bgcolor="#a0ddf0">
          <xsl:value-of select="@size"/>&#160;
        </td>
      </tr>
    </xsl:template>

</xsl:stylesheet>
