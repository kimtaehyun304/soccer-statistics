import React, { useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import SignIn from './screens/SignIn';
import SignUp from './screens/SignUp';
import PlayerDetail from './screens/PlayerDetail';
import MyHeader from './my-component/MyHeader';

import { AuthProvider } from './my-component/AuthContext';
import TeamDetail from './screens/TeamDeatail';
import DesignedPlayerDetail from './screens/DesignedPlayerDetail';
import DesignedTeamDetail from './screens/DesignedTeamDetail';
import PlayerRanking from './screens/PlayerRanking';
import TeamRanking from './screens/TeamRanking';
import { Helmet } from 'react-helmet';
import Search from './screens/Search';
import Home from './screens/Home';

const router = createBrowserRouter([
  
  {
    path: "/",
    children: [
      {
        // home
        path: "",
        element: <Home />,
      },
      {
        // search
        path: "/search",
        element: <Search />,
      },
      {
        // profile
        path: "profile",
        //element: <Profile />,
      },
      {
        // signin
        path: "/signin",
        element: <SignIn />,
      },
      {
        // signup
        path: "/signup",
        element: <SignUp />,
      },
      {
        // PlayerDetail
        path: "/players/:keyword",
        element: <DesignedPlayerDetail />,
      },  
      {
        // teamDetail
        path: "/teams/:keyword",
        element: <DesignedTeamDetail/>,
      }, 
      {
        // playerRanking
        path: "/ranking/players",
        element: <PlayerRanking />,
      }, 
      {
        // teamRanking
        path: "/ranking/teams",
        element: <TeamRanking />,
      }, 
    ],
  },
]);

function App() {
  //const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태 관리
  return (
    <div>
      <AuthProvider>
      <Helmet>
        <title>SSS</title>
      </Helmet>
        <RouterProvider router={router} />
      </AuthProvider>
    </div>
  );
}

export default App;
