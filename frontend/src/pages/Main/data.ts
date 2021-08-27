import badaImageSrc from 'assets/images/bada.png';
import cheffeImageSrc from 'assets/images/cheffe.png';
import kimkimImageSrc from 'assets/images/kimkim.png';
import sakjungImageSrc from 'assets/images/sakjung.png';
import sallyImageSrc from 'assets/images/sally.png';
import sunImageSrc from 'assets/images/sun.png';
import yujoImageSrc from 'assets/images/yujo.png';

interface TeamMember {
  image: string;
  name: string;
  position: 'BE' | 'FE';
}

export const teamMembers: TeamMember[] = [
  {
    image: kimkimImageSrc,
    name: '김김',
    position: 'BE',
  },
  {
    image: badaImageSrc,
    name: '바다',
    position: 'BE',
  },
  {
    image: sakjungImageSrc,
    name: '삭정',
    position: 'BE',
  },
  {
    image: sallyImageSrc,
    name: '샐리',
    position: 'BE',
  },
  {
    image: sunImageSrc,
    name: '썬',
    position: 'FE',
  },
  {
    image: yujoImageSrc,
    name: '유조',
    position: 'FE',
  },
  {
    image: cheffeImageSrc,
    name: '체프',
    position: 'FE',
  },
];
