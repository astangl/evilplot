package com.cibo.evilplot.plot.components

import com.cibo.evilplot.colors.{Color, DefaultColors}
import com.cibo.evilplot.geometry.{Drawable, Extent, Rect, Text}
import com.cibo.evilplot.plot.Plot
import com.cibo.evilplot.plot.aesthetics.Theme

case class FacetLabel(
  position: Position,
  labels: Extent => Seq[Drawable],
  minExtent: Extent
) extends FacetedPlotComponent {
  override val repeated: Boolean = true
  override def size(plot: Plot): Extent = minExtent
  def render(plot: Plot, extent: Extent, row: Int, column: Int)(implicit theme: Theme): Drawable = {
    val ls = labels(extent)
    position match {
      case Position.Top | Position.Bottom => ls(column).center(extent.width)
      case Position.Right | Position.Left => ls(row).middle(extent.height)
      case _                              => throw new IllegalStateException(s"bad position: $position")
    }
  }
}

trait FacetLabelImplicits {
  protected val plot: Plot

  private def topBottomLabelFunc(
    drawables: Seq[Drawable],
    backgroundColor: Color
  )(extent: Extent): Seq[Drawable] = {
    val bg = Rect(extent) filled backgroundColor
    drawables.map(d => bg behind d.center(extent.width))
  }

  private def leftRightLabelFunc(
    drawables: Seq[Drawable],
    backgroundColor: Color
  )(extent: Extent): Seq[Drawable] = {
    val bg = Rect(extent) filled backgroundColor
    drawables.map(d => bg behind d.middle(extent.height))
  }

  private def maxHeight(drawables: Seq[Drawable]): Double = drawables.maxBy(_.extent.height).extent.height

  private def maxWidth(drawables: Seq[Drawable]): Double = drawables.maxBy(_.extent.width).extent.width

  /** Add a label above each facet.
    * @param labels A function to return the labels of the given size.
    * @param height The height of the labels.
    */
  def topLabels(
    labels: Extent => Seq[Drawable],
    height: Double
  ): Plot = FacetLabel(Position.Top, labels, Extent(0, height)) +: plot

  /** Add a label above each facet.
    * @param labels The labels for each facet.
    */
  def topLabels(
    labels: Seq[String]
  )(implicit theme: Theme): Plot = {
    val drawableLabels = labels.map(Text(_, theme.fonts.labelSize).padBottom(4))
    val func = topBottomLabelFunc(drawableLabels, theme.colors.background)(_)
    topLabels(func, maxHeight(drawableLabels))
  }

  /** Add a label below each facet.
    * @param labels A function to return the labels of the given size.
    * @param height The height of the labels.
    */
  def bottomLabels(
    labels: Extent => Seq[Drawable],
    height: Double
  ): Plot = FacetLabel(Position.Bottom, labels, Extent(0, height)) +: plot

  /** Add a label below each facet.
    * @param labels The labels for each facet.
    */
  def bottomLabels(
    labels: Seq[String]
  )(implicit theme: Theme): Plot = {
    val drawableLabels = labels.map(Text(_, theme.fonts.labelSize).padTop(4))
    val func = topBottomLabelFunc(drawableLabels, theme.colors.background)(_)
    bottomLabels(func, maxHeight(drawableLabels))
  }

  /** Add a label to the right of each facet.
    * @param labels A function to return the labels of the given size.
    * @param width The width of the labels.
    */
  def rightLabels(
    labels: Extent => Seq[Drawable],
    width: Double
  ): Plot = FacetLabel(Position.Right, labels, Extent(width, 0)) +: plot

  /** Add a label to the right of each facet. */
  def rightLabels(
    labels: Seq[String]
  )(implicit theme: Theme): Plot = {
    val drawableLabels = labels.map(Text(_, theme.fonts.labelSize).rotated(90).padLeft(4))
    val func = leftRightLabelFunc(drawableLabels, theme.colors.background)(_)
    rightLabels(func, maxWidth(drawableLabels))
  }

  /** Add a label to the left of each facet.
    * @param labels A function to return the labels of the given size.
    * @param width The width of the labels.
    */
  def leftLabels(
    labels: Extent => Seq[Drawable],
    width: Double
  ): Plot = FacetLabel(Position.Left, labels, Extent(width, 0)) +: plot

  /** Add a label to the left of each facet. */
  def leftLabels(
    labels: Seq[String]
  )(implicit theme: Theme): Plot = {
    val drawableLabels = labels.map(Text(_, theme.fonts.labelSize).rotated(270).padRight(4))
    val func = leftRightLabelFunc(drawableLabels, theme.colors.background)(_)
    leftLabels(func, maxWidth(drawableLabels))
  }
}
