package org.swistowski.vaulthelper.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Html;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.fragments.AdFragment;
import org.swistowski.vaulthelper.fragments.ItemListFragment;
import org.swistowski.vaulthelper.fragments.MaterialFragment;
import org.swistowski.vaulthelper.models.Character;
import org.swistowski.vaulthelper.storage.Characters;
import org.swistowski.vaulthelper.storage.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = "ItemsFragmentPagerAdapter";
    private final List<Page> mPages = new ArrayList<>();

    public ItemsFragmentPagerAdapter(FragmentManager fm, Context context, boolean isPremium) {
        super(fm);
        if (!isPremium) {
            mPages.add(new Page(context.getString(R.string.ad), new FragmentProvider() {
                @Override
                public Fragment getFragment() {
                    return AdFragment.newInstance();
                }
            }));
        }

        if (Characters.getInstance().all() != null) {
            for (final Character character : Characters.getInstance().all()) {
                Page page = new Page(Html.fromHtml(character.getLabel()), new FragmentProvider() {
                    @Override
                    public Fragment getFragment() {
                        return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, character.getId());
                    }
                });

                mPages.add(page);
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

        if (Items.getInstance().allAsMap().size() > 0) {
            mPages.add(new Page(context.getString(R.string.vault), new FragmentProvider() {

                @Override
                public Fragment getFragment() {
                    return ItemListFragment.newInstance(ItemListFragment.DIRECTION_TO, Items.VAULT_ID);
                }
            }));
        }
        mPages.add(new Page(context.getString(R.string.materials), new FragmentProvider() {
            @Override
            public Fragment getFragment() {
                return MaterialFragment.newInstance();
            }
        }));
        if (isPremium) {
            mPages.add(new Page(context.getString(R.string.ad), new FragmentProvider() {
                @Override
                public Fragment getFragment() {
                    return AdFragment.newInstance();
                }
            }));
        }
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setIsPremium(Context context) {
        if (this.mPages.get(0).label == context.getString(R.string.ad)) {
            Page ad_page = mPages.get(0);
            for (int i = 0; i < mPages.size() - 1; i++) {
                mPages.set(i, mPages.get(i + 1));
            }
            mPages.set(mPages.size() - 1, ad_page);
            try {
                notifyDataSetChanged();
            } catch (IllegalStateException e) {
                // really rare issue, when notifiDataSetChanged is called when screen is changed from horizontal to vertical
            }
        } else {
        }

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
