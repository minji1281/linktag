package com.linktag.linkapp.ui.arclayout;

import android.content.Context;

import com.linktag.linkapp.R;
import com.ogaclejapan.arclayout.Arc;

public enum Demo {

  //Basic
  CENTER(R.string.title_center, R.string.note_center, R.layout.arc_large, Arc.CENTER),
  LEFT(R.string.title_left, R.string.note_left, R.layout.arc_medium, Arc.LEFT),
  RIGHT(R.string.title_right, R.string.note_right, R.layout.arc_medium, Arc.RIGHT),
  TOP(R.string.title_top, R.string.note_top, R.layout.arc_medium, Arc.TOP),
  TOP_LEFT(R.string.title_top_left, R.string.note_top_left, R.layout.arc_small, Arc.TOP_LEFT),
  TOP_RIGHT(R.string.title_top_right, R.string.note_top_right, R.layout.arc_small, Arc.TOP_RIGHT),
  BOTTOM(R.string.title_bottom, R.string.note_bottom, R.layout.arc_medium, Arc.BOTTOM),
  BOTTOM_LEFT(R.string.title_bottom_left, R.string.note_bottom_left, R.layout.arc_small,
      Arc.BOTTOM_LEFT),
  BOTTOM_RIGHT(R.string.title_bottom_right, R.string.note_bottom_right, R.layout.arc_small,
      Arc.BOTTOM_RIGHT),


  ADVANCED_PATH(R.string.title_advanced_path, 0, 0, null) {
    @Override
    public void startActivity(Context context) {
      arclayoutMain.startActivity(context, this);
    }
  };

  public final int titleResId;
  public final int noteResId;
  public final int layoutResId;
  public final Arc arc;

  Demo(int titleResId, int noteResId, int layoutResId, Arc arc) {
    this.titleResId = titleResId;
    this.noteResId = noteResId;
    this.layoutResId = layoutResId;
    this.arc = arc;
  }

  public void startActivity(Context context) {
    arclayoutMain.startActivity(context, this);
  }

}
