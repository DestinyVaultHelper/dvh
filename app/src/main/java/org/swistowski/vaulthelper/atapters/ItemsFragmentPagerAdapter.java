package org.swistowski.vaulthelper.atapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.fragments.AdFragment;
import org.swistowski.vaulthelper.fragments.ItemListFragment;
import org.swistowski.vaulthelper.fragments.SettingsFragment;
import org.swistowski.vaulthelper.models.Character;
import org.swistowski.vaulthelper.util.Data;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Page> mPages = new ArrayList<Page>();

    public ItemsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        if (Data.getInstance().getCharacters() != null) {
            for (final Character character : Data.getInstance().getCharacters()) {
                mPages.add(new Page(character.toString(), new FragmentProvider() {
                    @Override
                    public Fragment getFragment() {
                        return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, character.getId());
                    }
                }));
                /*
                  Future functionality, move from character too
                "▼ " +
                mPages.add(new Page("▲ " + character.toString(), new FragmentProvider() {
                    @Override
                    public Fragment getFragment() {
                        return ItemListFragment.newInstance(ItemListFragment.DIRECTION_FROM, character.getId());
                    }
                }));
                */
            }
        }

        if (Data.getInstance().getItems().size() > 0) {
            mPages.add(new Page(context.getString(R.string.vault), new FragmentProvider() {

                @Override
                public Fragment getFragment() {
                    return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, Data.VAULT_ID);
                }
            }));
        }
        if (mPages.size() == 0) {
            mPages.add(new Page(context.getString(R.string.settings), new FragmentProvider() {
                @Override
                public Fragment getFragment() {
                    return SettingsFragment.newInstance();
                }
            }));
        }
        mPages.add(new Page(context.getString(R.string.ad), new FragmentProvider() {
            @Override
            public Fragment getFragment() {
                return AdFragment.newInstance();
            }
        }));
    }

    @Override
    public Fragment getItem(int i) {
        return mPages.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).getLabel();
    }

    private interface FragmentProvider {
        public Fragment getFragment();
    }

    private class Page {
        private final CharSequence label;
        private final FragmentProvider fp;

        public Page(CharSequence label, FragmentProvider fp) {
            this.label = label;
            this.fp = fp;
        }

        public CharSequence getLabel() {
            return label;
        }

        public Fragment getFragment() {
            return fp.getFragment();
        }
    }
}
